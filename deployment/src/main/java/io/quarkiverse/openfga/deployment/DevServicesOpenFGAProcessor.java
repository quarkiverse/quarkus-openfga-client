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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkiverse.openfga.client.AuthorizationModelsClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.api.VertxWebClientFactory;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.dto.CreateStoreRequest;
import io.quarkiverse.openfga.client.model.dto.WriteBody;
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
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import io.quarkus.deployment.logging.LoggingSetupBuildItem;
import io.quarkus.devservices.common.ContainerLocator;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.runtime.util.ClassPathUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;

public class DevServicesOpenFGAProcessor {

    private static final Logger log = Logger.getLogger(DevServicesOpenFGAProcessor.class);
    static final String OPEN_FGA_VERSION = "v0.4.3";
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
    static final Duration INIT_OP_MAX_WAIT = Duration.ofSeconds(5);
    public static final String HEALTH_ENDPOINT = "/healthz";

    private static volatile RunningDevService devService;
    private static volatile DevServicesOpenFGAConfig capturedDevServicesConfiguration;
    private static volatile boolean first = true;

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    public DevServicesResultBuildItem startContainers(OpenFGABuildTimeConfig config,
            Optional<ConsoleInstalledBuildItem> consoleInstalledBuildItem,
            LaunchModeBuildItem launchMode,
            DockerStatusBuildItem dockerStatusBuildItem,
            CuratedApplicationShutdownBuildItem closeBuildItem,
            LoggingSetupBuildItem loggingSetupBuildItem,
            GlobalDevServicesConfig devServicesConfig,
            BuildProducer<DevServicesResultBuildItem> devServicesResults) {

        DevServicesOpenFGAConfig currentDevServicesConfiguration = config.devservices;

        // figure out if we need to shut down and restart any existing OpenFGA container
        // if not and the OpenFGA container have already started we just return
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
                    devServicesConfig.timeout);
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
        if (!devServicesConfig.enabled.orElse(true)) {
            // explicitly disabled
            log.debug("Not starting devservices for OpenFGA as it has been disabled in the config");
            return null;
        }

        boolean needToStart = !ConfigUtils.isPropertyPresent(URL_CONFIG_KEY);
        if (!needToStart) {
            log.debug("Not starting devservices for default OpenFGA client as url has been provided");
            return null;
        }

        if (!dockerStatusBuildItem.isDockerAvailable()) {
            log.warn("Please configure " + URL_CONFIG_KEY + " or get a working docker instance");
            return null;
        }

        DockerImageName dockerImageName = DockerImageName.parse(devServicesConfig.imageName.orElse(OPEN_FGA_IMAGE))
                .asCompatibleSubstituteFor(OPEN_FGA_IMAGE);

        final Supplier<RunningDevService> defaultOpenFGAInstanceSupplier = () -> {

            QuarkusOpenFGAContainer container = new QuarkusOpenFGAContainer(dockerImageName, devServicesConfig.httpPort,
                    devServicesConfig.grpcPort, devServicesConfig.playgroundPort,
                    devServicesConfig.serviceName)
                    .withNetwork(Network.SHARED)
                    .waitingFor(Wait.forHttp(HEALTH_ENDPOINT).forPort(OPEN_FGA_EXPOSED_HTTP_PORT));

            timeout.ifPresent(container::withStartupTimeout);

            log.info("Starting OpenFGA...");

            container.start();

            var devServicesConfigProperties = new HashMap<String, String>();

            withAPI(container.getHost(), container.getHttpPort(), (instanceURL, api) -> {

                devServicesConfigProperties.put(URL_CONFIG_KEY, instanceURL.toExternalForm());

                String storeId;
                try {

                    log.info("Initializing authorization store...");

                    storeId = api.createStore(new CreateStoreRequest(devServicesConfig.storeName))
                            .await().atMost(INIT_OP_MAX_WAIT)
                            .getId();

                    devServicesConfigProperties.put(STORE_ID_CONFIG_KEY, storeId);

                } catch (Throwable e) {
                    throw new RuntimeException("Store initialization failed", e);
                }

                loadAuthorizationModelDefinition(api, devServicesConfig)
                        .ifPresentOrElse(authModelDef -> {

                            String authModelId;
                            try {
                                log.info("Initializing authorization model...");

                                authModelId = api.writeAuthorizationModel(storeId, authModelDef)
                                        .await()
                                        .atMost(INIT_OP_MAX_WAIT)
                                        .getAuthorizationModelId();

                                devServicesConfigProperties.put(AUTHORIZATION_MODEL_ID_CONFIG_KEY, authModelId);

                            } catch (Exception e) {
                                throw new RuntimeException("Model initialization failed", e);
                            }

                            loadAuthorizationTuples(api, devServicesConfig)
                                    .ifPresent(authTuples -> {
                                        try {
                                            log.info("Initializing authorization tuples...");

                                            api.write(storeId, new WriteBody(new TupleKeys(authTuples), null, authModelId))
                                                    .await()
                                                    .atMost(INIT_OP_MAX_WAIT);

                                        } catch (Exception e) {
                                            throw new RuntimeException("Tuples initialization failed", e);
                                        }
                                    });
                        }, () -> {
                            if (devServicesConfig.authorizationTuples.isPresent()
                                    || devServicesConfig.authorizationTuplesLocation.isPresent()) {
                                log.warn("No authorization model configured, no tuples will not be initialized");
                            }
                        });

                return null;
            });

            return new RunningDevService(FEATURE, container.getContainerId(), container::close, devServicesConfigProperties);
        };

        return openFGAContainerLocator
                .locateContainer(devServicesConfig.serviceName, devServicesConfig.shared, launchMode.getLaunchMode())
                .map(containerAddress -> {

                    Map<String, String> devServicesConfigProperties = new HashMap<>();

                    withAPI(containerAddress.getHost(), containerAddress.getPort(), (instanceURL, api) -> {

                        devServicesConfigProperties.put(URL_CONFIG_KEY, instanceURL.toExternalForm());

                        String storeId;
                        try {
                            var client = new OpenFGAClient(api);

                            storeId = client.listAllStores().await().atMost(INIT_OP_MAX_WAIT)
                                    .stream().filter(store -> store.getName().equals(devServicesConfig.storeName))
                                    .map(Store::getId)
                                    .findFirst()
                                    .orElseThrow();

                            devServicesConfigProperties.put(STORE_ID_CONFIG_KEY, storeId);

                        } catch (Throwable x) {
                            throw new ConfigurationException(format("Could not find store '%s' in shared DevServices instance",
                                    devServicesConfig.storeName));
                        }

                        loadAuthorizationModelDefinition(api, devServicesConfig)
                                .ifPresent(authModelDef -> {
                                    try {
                                        var client = new AuthorizationModelsClient(api, Uni.createFrom().item(storeId));

                                        var authModelId = client.listAll().await().atMost(INIT_OP_MAX_WAIT)
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

    private static Optional<TypeDefinitions> loadAuthorizationModelDefinition(API api,
            DevServicesOpenFGAConfig devServicesConfig) {
        return devServicesConfig.authorizationModel
                .or(() -> {
                    return devServicesConfig.authorizationModelLocation
                            .map(location -> {
                                try {
                                    var authModelPath = resolveModelPath(location);
                                    return Files.readString(authModelPath);
                                } catch (Throwable x) {
                                    throw new RuntimeException(
                                            format("Unable to load authorization model from '%s'", location));
                                }
                            });
                })
                .map(authModelJSON -> {
                    try {
                        return api.parseModel(authModelJSON);
                    } catch (Throwable t) {
                        throw new RuntimeException("Unable to parse authorization model", t);
                    }
                });
    }

    private static Optional<List<TupleKey>> loadAuthorizationTuples(API api,
            DevServicesOpenFGAConfig devServicesConfig) {
        return devServicesConfig.authorizationTuples
                .or(() -> {
                    return devServicesConfig.authorizationTuplesLocation
                            .map(location -> {
                                try {
                                    var authModelPath = resolveModelPath(location);
                                    return Files.readString(authModelPath);
                                } catch (Throwable x) {
                                    throw new RuntimeException(
                                            format("Unable to load authorization tuples from '%s'", location));
                                }
                            });
                })
                .map(authTuplesJSON -> {
                    try {
                        return api.parseTuples(authTuplesJSON);
                    } catch (Throwable t) {
                        throw new RuntimeException("Unable to parse authorization tuples", t);
                    }
                });
    }

    private static Path resolveModelPath(String location) throws IOException {
        location = normalizeLocation(location);
        if (location.startsWith("filesystem:")) {
            return Path.of(location.substring("filesystem:".length()));
        }

        var classpathPath = new AtomicReference<Path>();
        ClassPathUtils.consumeAsPaths(Thread.currentThread().getContextClassLoader(), location, classpathPath::set);

        return classpathPath.get();
    }

    private static String normalizeLocation(String location) {
        // Strip any 'classpath:' protocol prefixes because they are assumed
        // but not recognized by ClassLoader.getResources()
        if (location.startsWith("classpath:")) {
            location = location.substring("classpath:".length());
            if (location.startsWith("/")) {
                location = location.substring(1);
            }
        }
        if (!location.endsWith("/")) {
            location += "/";
        }
        return location;
    }

    private static void withAPI(String host, Integer port, BiFunction<URL, API, Void> apiConsumer) {
        URL instanceURL;
        try {
            instanceURL = new URL("http", host, port, "");
        } catch (MalformedURLException e) {
            // Should not happen
            throw new RuntimeException(e);
        }

        Vertx vertx = Vertx.vertx();
        try (var api = new API(VertxWebClientFactory.create(instanceURL, vertx), Optional.empty())) {

            apiConsumer.apply(instanceURL, api);

        } finally {
            vertx.close().await().atMost(INIT_OP_MAX_WAIT);
        }
    }

    private static class QuarkusOpenFGAContainer extends GenericContainer<QuarkusOpenFGAContainer> {
        OptionalInt fixedExposedHttpPort;
        OptionalInt fixedExposedGrpcPort;
        OptionalInt fixedExposedPlaygroundPort;

        public QuarkusOpenFGAContainer(DockerImageName dockerImageName, OptionalInt fixedExposedHttpPort,
                OptionalInt fixedExposedGrpcPort, OptionalInt fixedExposedPlaygroundPort, String serviceName) {
            super(dockerImageName);
            this.fixedExposedHttpPort = fixedExposedHttpPort;
            this.fixedExposedGrpcPort = fixedExposedGrpcPort;
            this.fixedExposedPlaygroundPort = fixedExposedPlaygroundPort;
            withCommand("run");
            withNetwork(Network.SHARED);
            if (serviceName != null) { // Only adds the label in dev mode.
                withLabel(DEV_SERVICE_LABEL, serviceName);
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
    }
}
