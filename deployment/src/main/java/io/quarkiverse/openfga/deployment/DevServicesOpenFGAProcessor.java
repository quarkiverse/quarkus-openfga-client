package io.quarkiverse.openfga.deployment;

import static io.quarkiverse.openfga.deployment.OpenFGAProcessor.FEATURE;
import static java.lang.String.format;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import io.quarkus.bootstrap.runner.ClassLoadingResource;
import io.quarkus.commons.classloading.ClassLoaderHelper;
import org.jboss.logging.Logger;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.openfga.OpenFGAContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkiverse.openfga.client.AuthorizationModelsClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.api.VertxWebClientFactory;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.dto.CreateStoreRequest;
import io.quarkiverse.openfga.client.model.dto.WriteAuthorizationModelRequest;
import io.quarkiverse.openfga.client.model.dto.WriteRequest;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CuratedApplicationShutdownBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem.RunningDevService;
import io.quarkus.deployment.builditem.DockerStatusBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.console.ConsoleInstalledBuildItem;
import io.quarkus.deployment.console.StartupLogCompressor;
import io.quarkus.deployment.dev.devservices.DevServicesConfig;
import io.quarkus.deployment.logging.LoggingSetupBuildItem;
import io.quarkus.deployment.util.FileUtil;
import io.quarkus.devservices.common.ContainerLocator;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.runtime.util.ClassPathUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;

public class DevServicesOpenFGAProcessor {

    private static final Logger log = Logger.getLogger(DevServicesOpenFGAProcessor.class);

    static final String OPEN_FGA_VERSION = "v1.8.4";
    static final String OPEN_FGA_IMAGE = "openfga/openfga:" + OPEN_FGA_VERSION;
    static final int OPEN_FGA_EXPOSED_HTTP_PORT = 8080;
    static final int OPEN_FGA_EXPOSED_GRPC_PORT = 8081;
    static final int OPEN_FGA_EXPOSED_PLAY_PORT = 3000;
    static final String DEV_SERVICE_LABEL = "quarkus-dev-service-openfga";
    static final String CONFIG_PREFIX = "quarkus.openfga.";
    static final String URL_CONFIG_KEY = CONFIG_PREFIX + "url";
    static final String STORE_ID_CONFIG_KEY = CONFIG_PREFIX + "store";
    static final String AUTHORIZATION_MODEL_ID_CONFIG_KEY = CONFIG_PREFIX + "authorization-model-id";
    static final ContainerLocator openFGAContainerLocator = new ContainerLocator(DEV_SERVICE_LABEL, OPEN_FGA_EXPOSED_HTTP_PORT);

    private static volatile RunningDevService devService;
    private static volatile DevServicesOpenFGAConfig capturedDevServicesConfiguration;
    private static volatile boolean first = true;

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = DevServicesConfig.Enabled.class)
    public DevServicesResultBuildItem startContainers(OpenFGABuildTimeConfig config,
            Optional<ConsoleInstalledBuildItem> consoleInstalledBuildItem,
            LaunchModeBuildItem launchMode,
            DockerStatusBuildItem dockerStatusBuildItem,
            CuratedApplicationShutdownBuildItem closeBuildItem,
            LoggingSetupBuildItem loggingSetupBuildItem,
            DevServicesConfig devServicesConfig,
            BuildProducer<DevServicesResultBuildItem> devServicesResults) {

        DevServicesOpenFGAConfig currentDevServicesConfiguration = config.devservices();

        // figure out if we need to shut down and restart any existing OpenFGA container
        // if not, and the OpenFGA container has already started we just return
        if (devService != null) {
            boolean restartRequired = !currentDevServicesConfiguration.equals(capturedDevServicesConfiguration);
            if (!restartRequired) {
                return devService.toBuildItem();
            }
            try {
                devService.close();
            } catch (Throwable e) {
                log.error("Failed to stop OpenFGA container", e);
            }
            devService = null;
            capturedDevServicesConfiguration = null;
        }

        capturedDevServicesConfiguration = currentDevServicesConfiguration;

        StartupLogCompressor compressor = new StartupLogCompressor(
                (launchMode.isTest() ? "(test) " : "") + "OpenFGA Dev Services Starting:", consoleInstalledBuildItem,
                loggingSetupBuildItem);
        try {
            devService = startContainer(dockerStatusBuildItem, currentDevServicesConfiguration, launchMode,
                    devServicesConfig.timeout());
            if (devService != null) {

                if (devService.isOwner()) {
                    log.info("Dev Services for OpenFGA started.");
                    log.infof("Other Quarkus applications in dev mode will find the "
                            + "instance automatically. For Quarkus applications in production mode, you can connect to"
                            + " this by starting your application with -D%s=%s",
                            URL_CONFIG_KEY, devService.getConfig().get(URL_CONFIG_KEY));
                }
            } else {
                return null;
            }
        } catch (Throwable t) {
            compressor.closeAndDumpCaptured();
            throw new RuntimeException(t);
        } finally {
            compressor.close();
        }

        if (first) {
            first = false;
            Runnable closeTask = () -> {
                if (devService != null) {
                    try {
                        devService.close();
                    } catch (Throwable t) {
                        log.error("Failed to stop OpenFGA container", t);
                    }
                    devService = null;
                    log.info("Dev Services for OpenFGA shut down.");
                }
                first = true;
                capturedDevServicesConfiguration = null;
            };
            closeBuildItem.addCloseTask(closeTask, true);
        }

        return devService.toBuildItem();
    }

    private RunningDevService startContainer(DockerStatusBuildItem dockerStatusBuildItem,
            DevServicesOpenFGAConfig devServicesConfig, LaunchModeBuildItem launchMode, Optional<Duration> timeout) {
        if (!devServicesConfig.enabled().orElse(true)) {
            // explicitly disabled
            log.debug("Not starting devservices for OpenFGA as it has been disabled in the config");
            return null;
        }

        boolean needToStart = !ConfigUtils.isPropertyPresent(URL_CONFIG_KEY);
        if (!needToStart) {
            log.debug("Not starting devservices for default OpenFGA client as url has been provided");
            return null;
        }

        if (!dockerStatusBuildItem.isContainerRuntimeAvailable()) {
            log.warn("Please configure " + URL_CONFIG_KEY + " or get a working docker instance");
            return null;
        }

        DockerImageName dockerImageName = DockerImageName.parse(devServicesConfig.imageName().orElse(OPEN_FGA_IMAGE))
                .asCompatibleSubstituteFor(OPEN_FGA_IMAGE);

        var tlsEnabled = devServicesConfig.tls().pemCertificatePath().isPresent();

        final Supplier<RunningDevService> defaultOpenFGAInstanceSupplier = () -> {

            var container = (QuarkusOpenFGAContainer) new QuarkusOpenFGAContainer(dockerImageName,
                    devServicesConfig.httpPort(), devServicesConfig.grpcPort(), devServicesConfig.playgroundPort(),
                    devServicesConfig.serviceName(), devServicesConfig.tls())
                    .withNetwork(Network.SHARED);

            timeout.ifPresent(container::withStartupTimeout);

            log.info("Starting OpenFGA...");

            container.start();

            var devServicesConfigProperties = new HashMap<String, String>();

            withAPI(container.getHost(), container.getHttpPort(), tlsEnabled, devServicesConfig.startupTimeout(),
                    (instanceURL, api) -> {

                        devServicesConfigProperties.put(URL_CONFIG_KEY, instanceURL.toExternalForm());

                        String storeId;
                        try {

                            log.info("Initializing authorization store...");

                            storeId = api.createStore(CreateStoreRequest.builder().name(devServicesConfig.storeName()).build())
                                    .await().atMost(devServicesConfig.startupTimeout())
                                    .id();

                            devServicesConfigProperties.put(STORE_ID_CONFIG_KEY, storeId);

                        } catch (Throwable e) {
                            throw new RuntimeException("Store initialization failed", e);
                        }

                        loadAuthorizationModelDefinition(devServicesConfig)
                                .ifPresentOrElse(schema -> {

                                    String authModelId;
                                    try {
                                        log.info("Initializing authorization model...");

                                        var request = WriteAuthorizationModelRequest.builder()
                                                .schemaVersion(schema.getSchemaVersion())
                                                .typeDefinitions(schema.getTypeDefinitions())
                                                .conditions(schema.getConditions())
                                                .build();
                                        authModelId = api.writeAuthorizationModel(storeId, request)
                                                .await()
                                                .atMost(devServicesConfig.startupTimeout())
                                                .authorizationModelId();

                                        devServicesConfigProperties.put(AUTHORIZATION_MODEL_ID_CONFIG_KEY, authModelId);

                                    } catch (Exception e) {
                                        throw new RuntimeException("Model initialization failed", e);
                                    }

                                    loadAuthorizationTuples(devServicesConfig)
                                            .ifPresent(authTuples -> {
                                                try {
                                                    log.info("Initializing authorization tuples...");

                                                    var writeRequest = WriteRequest.builder()
                                                            .authorizationModelId(authModelId)
                                                            .writes(WriteRequest.Writes.of(authTuples))
                                                            .build();
                                                    api.write(storeId, writeRequest)
                                                            .await()
                                                            .atMost(devServicesConfig.startupTimeout());

                                                } catch (Exception e) {
                                                    throw new RuntimeException("Tuples initialization failed", e);
                                                }
                                            });
                                }, () -> {
                                    if (devServicesConfig.authorizationTuples().isPresent()
                                            || devServicesConfig.authorizationTuplesLocation().isPresent()) {
                                        log.warn("No authorization model configured, no tuples will not be initialized");
                                    }
                                });

                        return null;
                    });

            return new RunningDevService(FEATURE, container.getContainerId(), container::close, devServicesConfigProperties);
        };

        return openFGAContainerLocator
                .locateContainer(devServicesConfig.serviceName(), devServicesConfig.shared(), launchMode.getLaunchMode())
                .map(containerAddress -> {

                    Map<String, String> devServicesConfigProperties = new HashMap<>();

                    withAPI(containerAddress.getHost(), containerAddress.getPort(), tlsEnabled,
                            devServicesConfig.startupTimeout(), (instanceURL, api) -> {

                                devServicesConfigProperties.put(URL_CONFIG_KEY, instanceURL.toExternalForm());

                                String storeId;
                                try {
                                    var client = new OpenFGAClient(api);

                                    storeId = client.listAllStores().await()
                                            .atMost(devServicesConfig.startupTimeout())
                                            .stream().filter(store -> store.getName().equals(devServicesConfig.storeName()))
                                            .map(Store::getId)
                                            .findFirst()
                                            .orElseThrow();

                                    devServicesConfigProperties.put(STORE_ID_CONFIG_KEY, storeId);

                                } catch (Throwable x) {
                                    throw new ConfigurationException(
                                            format("Could not find store '%s' in shared DevServices instance",
                                                    devServicesConfig.storeName()));
                                }

                                loadAuthorizationModelDefinition(devServicesConfig)
                                        .ifPresent(authModelDef -> {
                                            try {
                                                var client = new AuthorizationModelsClient(api, Uni.createFrom().item(storeId));

                                                var authModelId = client.listAll().await()
                                                        .atMost(devServicesConfig.startupTimeout())
                                                        .stream()
                                                        .filter(item -> item.getTypeDefinitions()
                                                                .equals(authModelDef.getTypeDefinitions()))
                                                        .map(AuthorizationModel::getId)
                                                        .findFirst()
                                                        .orElseThrow();

                                                devServicesConfigProperties.put(AUTHORIZATION_MODEL_ID_CONFIG_KEY, authModelId);

                                            } catch (Throwable x) {
                                                throw new ConfigurationException(
                                                        "Could not find authorization model in shared DevServices instance");
                                            }
                                        });

                                return null;
                            });

                    return new RunningDevService(FEATURE, containerAddress.getId(), null, devServicesConfigProperties);
                })
                .orElseGet(defaultOpenFGAInstanceSupplier);
    }

    private static Optional<AuthorizationModelSchema> loadAuthorizationModelDefinition(
            DevServicesOpenFGAConfig devServicesConfig) {
        return devServicesConfig.authorizationModel()
                .or(() -> devServicesConfig.authorizationModelLocation()
                        .map(location -> {
                            try {
                                return readLocation(location);
                            } catch (Throwable x) {
                                throw new RuntimeException(
                                        format("Unable to load authorization model from '%s'", location), x);
                            }
                        }))
                .map(authModelJSON -> {
                    try {
                        return AuthorizationModelSchema.parse(authModelJSON);
                    } catch (Throwable t) {
                        throw new RuntimeException("Unable to parse authorization model", t);
                    }
                });
    }

    private static Optional<Collection<RelTupleKeyed>> loadAuthorizationTuples(DevServicesOpenFGAConfig devServicesConfig) {
        return devServicesConfig.authorizationTuples()
                .or(() -> devServicesConfig.authorizationTuplesLocation()
                        .map(location -> {
                            try {
                                return readLocation(location);
                            } catch (Throwable x) {
                                throw new RuntimeException(
                                        format("Unable to load authorization tuples from '%s'", location), x);
                            }
                        }))
                .map(authTuplesJSON -> {
                    try {
                        return RelTupleKeys.parseList(authTuplesJSON).getTupleKeys();
                    } catch (Throwable t) {
                        throw new RuntimeException("Unable to parse authorization tuples", t);
                    }
                });
    }

    private static String readLocation(String location) throws IOException {
        if (location.startsWith("filesystem:")) {
            var path = Path.of(location.substring("filesystem:".length()));
            return Files.readString(path);
        }

        URL resourceURL = getLocationResource(location);
        return ClassPathUtils.readStream(resourceURL, (stream) -> {
            try {
                var contents = FileUtil.readFileContents(stream);
                return new String(contents);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read resource: " + location, e);
            }
        });
    }

    private static Path resolvePath(String location) throws IOException {
        if (location.startsWith("filesystem:")) {
            return Path.of(location.substring("filesystem:".length()));
        }

        URL resourceURL = getLocationResource(location);
        return ClassPathUtils.toLocalPath(resourceURL);
    }

    private static URL getLocationResource(String location) throws IOException {

        // Strip any 'classpath:' protocol prefixes because they are assumed
        // but not recognized by ClassLoader.getResources()
        String resourceLocation;
        if (location.startsWith("classpath:")) {
            resourceLocation = location.substring("classpath:".length());
        } else {
            resourceLocation = location;
        }

        URL resourceURL = Thread.currentThread().getContextClassLoader().getResource(resourceLocation);
        if (resourceURL == null) {
            resourceURL = QuarkusClassLoader.getSystemResource(resourceLocation);
        }

        if (resourceURL == null) {
            throw new IOException("Resource not found: " + resourceLocation);
        }
        return resourceURL;
    }

    private static void withAPI(String host, Integer port, boolean tlsEnabled, Duration startupTimout,
            BiFunction<URL, API, Void> apiConsumer) {
        URL instanceURL;
        try {
            instanceURL = new URL(tlsEnabled ? "https" : "http", host, port, "");
        } catch (MalformedURLException e) {
            // Should not happen
            throw new RuntimeException(e);
        }

        Vertx vertx = Vertx.vertx();
        try (var api = new API(VertxWebClientFactory.create(instanceURL, vertx), null)) {

            apiConsumer.apply(instanceURL, api);

        } finally {
            vertx.close().await().atMost(startupTimout);
        }
    }

    private static class QuarkusOpenFGAContainer extends OpenFGAContainer {
        OptionalInt fixedExposedHttpPort;
        OptionalInt fixedExposedGrpcPort;
        OptionalInt fixedExposedPlaygroundPort;
        boolean tlsEnabled;

        public QuarkusOpenFGAContainer(DockerImageName dockerImageName, OptionalInt fixedExposedHttpPort,
                OptionalInt fixedExposedGrpcPort, OptionalInt fixedExposedPlaygroundPort,
                String serviceName, DevServicesOpenFGAConfig.Tls tls) {
            super(dockerImageName);
            this.fixedExposedHttpPort = fixedExposedHttpPort;
            this.fixedExposedGrpcPort = fixedExposedGrpcPort;
            this.fixedExposedPlaygroundPort = fixedExposedPlaygroundPort;
            withNetwork(Network.SHARED);
            if (serviceName != null) { // Only adds the label in dev mode.
                withLabel(DEV_SERVICE_LABEL, serviceName);
            }
            if (tls.pemKeyPath().isPresent() && tls.pemCertificatePath().isPresent()) {
                try {
                    var certPath = tls.pemCertificatePath()
                            .orElseThrow(() -> new IllegalStateException("Missing TLS certificate path"));
                    var keyPath = tls.pemKeyPath()
                            .orElseThrow(() -> new IllegalStateException("Missing TLS key path"));
                    withCommand("run", "--http-tls-enabled=true",
                            "--http-tls-cert=/tls/cert.pem", "--http-tls-key=/tls/key.pem");
                    withFileSystemBind(resolvePath(certPath).toAbsolutePath().toString(),
                            "/tls/cert.pem", BindMode.READ_ONLY);
                    withFileSystemBind(resolvePath(keyPath).toAbsolutePath().toString(),
                            "/tls/key.pem", BindMode.READ_ONLY);
                    waitingFor(Wait.forHttps("/healthz").forPort(8080)
                            .forResponsePredicate((response) -> response.contains("SERVING")).usingTls().allowInsecure());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to bind TLS certificate and key", e);
                }
                tlsEnabled = true;
            }
        }

        @Override
        protected void configure() {
            super.configure();

            if (fixedExposedHttpPort.isPresent()) {
                addFixedExposedPort(fixedExposedHttpPort.getAsInt(), OPEN_FGA_EXPOSED_HTTP_PORT);
            } else {
                addExposedPort(OPEN_FGA_EXPOSED_HTTP_PORT);
            }

            if (fixedExposedGrpcPort.isPresent()) {
                addFixedExposedPort(fixedExposedGrpcPort.getAsInt(), OPEN_FGA_EXPOSED_GRPC_PORT);
            } else {
                addExposedPort(OPEN_FGA_EXPOSED_GRPC_PORT);
            }

            if (fixedExposedPlaygroundPort.isPresent()) {
                addFixedExposedPort(fixedExposedPlaygroundPort.getAsInt(), OPEN_FGA_EXPOSED_PLAY_PORT);
            } else {
                addExposedPort(OPEN_FGA_EXPOSED_PLAY_PORT);
            }
        }

        public int getHttpPort() {
            if (fixedExposedHttpPort.isPresent()) {
                return fixedExposedHttpPort.getAsInt();
            }
            return super.getMappedPort(OPEN_FGA_EXPOSED_HTTP_PORT);
        }

        @Override
        public String getHttpEndpoint() {
            return (tlsEnabled ? "https" : "http") + "://" + this.getHost() + ":" + this.getMappedPort(8080);
        }
    }
}
