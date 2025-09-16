package io.quarkiverse.openfga.deployment;

import static io.quarkiverse.openfga.deployment.OpenFGAProcessor.FEATURE;
import static java.lang.String.format;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jboss.logging.Logger;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.openfga.OpenFGAContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkiverse.openfga.client.AuthorizationModelsClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.api.VertxWebClientFactory;
import io.quarkiverse.openfga.client.api.auth.PresharedKeyCredentialsProvider;
import io.quarkiverse.openfga.client.api.auth.UnauthenticatedCredentialsProvider;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.dto.CreateStoreRequest;
import io.quarkiverse.openfga.client.model.dto.WriteAuthorizationModelRequest;
import io.quarkiverse.openfga.client.model.dto.WriteRequest;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.*;
import io.quarkus.deployment.dev.devservices.DevServicesConfig;
import io.quarkus.deployment.logging.LoggingSetupBuildItem;
import io.quarkus.deployment.util.FileUtil;
import io.quarkus.devservices.common.*;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.runtime.util.ClassPathUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;

public class DevServicesOpenFGAProcessor {

    private static final Logger log = Logger.getLogger(DevServicesOpenFGAProcessor.class);

    public static final String OPEN_FGA_VERSION = "v1.10.0";
    public static final String OPEN_FGA_IMAGE_NAME = "openfga/openfga";
    public static final String OPEN_FGA_IMAGE = OPEN_FGA_IMAGE_NAME + ":" + OPEN_FGA_VERSION;
    public static final String DEV_SERVICE_LABEL = "quarkus-dev-service-openfga";
    public static final int OPEN_FGA_EXPOSED_HTTP_PORT = 8080;
    public static final int OPEN_FGA_EXPOSED_GRPC_PORT = 8081;
    public static final int OPEN_FGA_EXPOSED_PLAY_PORT = 3000;
    static final String CONFIG_PREFIX = "quarkus.openfga.";
    static final String URL_CONFIG_KEY = CONFIG_PREFIX + "url";
    static final String STORE_ID_CONFIG_KEY = CONFIG_PREFIX + "store";
    static final String AUTHORIZATION_MODEL_ID_CONFIG_KEY = CONFIG_PREFIX + "authorization-model-id";
    static final String CREDS_PREFIX = CONFIG_PREFIX + "credentials.";
    static final String CREDS_PRESHARED_PREFIX = CREDS_PREFIX + "preshared.";
    static final String CREDS_PRESHARED_KEY_KEY = CREDS_PRESHARED_PREFIX + "key";
    static final String CREDS_OIDC_PREFIX = CREDS_PREFIX + "oidc.";
    static final String CREDS_OIDC_ISSUER_KEY = CREDS_OIDC_PREFIX + "issuer";
    static final String CREDS_OIDC_AUDIENCE_KEY = CREDS_OIDC_PREFIX + "audience";
    static final String DEVSERVICES_PREFIX = "quarkus.devservices.";
    static final String AUTHN_PREFIX = DEVSERVICES_PREFIX + "openfga.";
    static final String AUTHN_PRESHARED_PREFIX = AUTHN_PREFIX + "authn.preshared.";
    static final String AUTHN_PRESHARED_KEYS_KEY = AUTHN_PRESHARED_PREFIX + "keys";
    static final String LOC_CLASSPATH_PREFIX = "classpath:";
    static final String LOC_FILESYSTEM_PREFIX = "filesystem:";
    static final String TEST_LAUNCH_MODE = CONFIG_PREFIX + "test-launch-mode";
    static final ContainerLocator openFGAContainerLocator = ContainerLocator
            .locateContainerWithLabels(OPEN_FGA_EXPOSED_HTTP_PORT, DEV_SERVICE_LABEL);

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = DevServicesConfig.Enabled.class)
    public void startContainers(OpenFGABuildTimeConfig config,
            LaunchModeBuildItem launchModeBuildItem,
            DockerStatusBuildItem dockerStatusBuildItem,
            CuratedApplicationShutdownBuildItem closeBuildItem,
            LoggingSetupBuildItem loggingSetupBuildItem,
            List<DevServicesSharedNetworkBuildItem> sharedNetworkBuildItem,
            DevServicesComposeProjectBuildItem composeProjectBuildItem,
            DevServicesConfig devServicesConfig,
            BuildProducer<DevServicesResultBuildItem> devServicesResults) {

        var launchMode = Optional.ofNullable(System.getProperty(TEST_LAUNCH_MODE))
                .flatMap(mode -> Arrays.stream(LaunchMode.values())
                        .filter(m -> m.getDefaultProfile().equals(mode)).findFirst())
                .orElse(launchModeBuildItem.getLaunchMode());

        DevServicesOpenFGAConfig openFGADevServiceConfig = config.devservices();

        if (!openFGADevServiceConfig.enabled().orElse(true)) {
            // explicitly disabled
            log.debug("Not starting devservices for OpenFGA as it has been disabled in the config");
            return;
        }

        var useSharedNetwork = DevServicesSharedNetworkBuildItem.isSharedNetworkRequired(devServicesConfig,
                sharedNetworkBuildItem);

        boolean needToStart = !ConfigUtils.isPropertyNonEmpty(URL_CONFIG_KEY);
        if (!needToStart) {
            log.debug("Not starting devservices for default OpenFGA client as url has been provided");
            return;
        }

        if (!dockerStatusBuildItem.isContainerRuntimeAvailable()) {
            log.warn("Requires configuration of '" + URL_CONFIG_KEY + "' or a working docker instance");
            return;
        }

        var dockerImageName = DockerImageName.parse(openFGADevServiceConfig.imageName().orElse(OPEN_FGA_IMAGE))
                .asCompatibleSubstituteFor(OPEN_FGA_IMAGE);

        var resolvedConfigProperties = new CompletableFuture<Map<String, String>>();

        var configPropertyResolvers = new HashMap<String, Function<StartableContainer<QuarkusOpenFGAContainer>, String>>();
        addContainerConfigurationResolvers(openFGADevServiceConfig, resolvedConfigProperties, configPropertyResolvers::put);
        addCredentialsConfiguration(openFGADevServiceConfig, (k, v) -> configPropertyResolvers.put(k, s -> v));

        final Supplier<DevServicesResultBuildItem> startSupplier = () -> DevServicesResultBuildItem.owned()
                .name(FEATURE)
                .serviceName(openFGADevServiceConfig.serviceName())
                .serviceConfig(config)
                .description("OpenFGA DevServices Instance")
                .startable(() -> {
                    var container = new QuarkusOpenFGAContainer(dockerImageName, openFGADevServiceConfig,
                            composeProjectBuildItem.getDefaultNetworkId(), useSharedNetwork);
                    if (openFGADevServiceConfig.shared()) {
                        container = container.withSharedServiceLabel(launchMode, DEV_SERVICE_LABEL,
                                openFGADevServiceConfig.serviceName());
                    }
                    return new StartableContainer<>(container);
                })
                .postStartHook(startable -> {

                    try {

                        var configProperties = new HashMap<String, String>();
                        var container = startable.getContainer();

                        withAPI(container.getHost(), container.getHttpPort(), openFGADevServiceConfig, (instanceURL, api) -> {

                            configProperties.put(URL_CONFIG_KEY, instanceURL.toExternalForm());

                            var storeId = createStore(api, openFGADevServiceConfig);
                            configProperties.put(STORE_ID_CONFIG_KEY, storeId);

                            loadAuthorizationModelDefinition(openFGADevServiceConfig)
                                    .ifPresentOrElse(schema -> {

                                        var authModelId = loadAuthorizationModel(api, storeId, schema, openFGADevServiceConfig);
                                        configProperties.put(AUTHORIZATION_MODEL_ID_CONFIG_KEY, authModelId);

                                        loadAuthorizationTuplesDefinition(openFGADevServiceConfig)
                                                .ifPresent(authTuples -> {
                                                    loadAuthorizationTuples(api, storeId, authModelId, authTuples,
                                                            openFGADevServiceConfig);
                                                });
                                    }, () -> {
                                        if (openFGADevServiceConfig.authorizationTuples().isPresent()
                                                || openFGADevServiceConfig.authorizationTuplesLocation().isPresent()) {
                                            log.warn(
                                                    "No authorization model configured, no tuples will not be initialized");
                                        }
                                    });
                        });

                        resolvedConfigProperties.complete(configProperties);
                    } catch (Throwable t) {
                        resolvedConfigProperties.completeExceptionally(
                                new ConfigurationException("Configuration not available, failed to initialize container"));
                        throw t;
                    }
                })
                .configProvider(configPropertyResolvers)
                .build();

        devServicesResults.produce(
                openFGAContainerLocator
                        .locateContainer(openFGADevServiceConfig.serviceName(), openFGADevServiceConfig.shared(), launchMode)
                        .or(() -> ComposeLocator.locateContainer(composeProjectBuildItem,
                                List.of(openFGADevServiceConfig.imageName().orElse(OPEN_FGA_IMAGE_NAME)),
                                OPEN_FGA_EXPOSED_HTTP_PORT, launchMode, useSharedNetwork))
                        .map(containerAddress -> {

                            var devServicesConfigProperties = new HashMap<String, String>();
                            resolveContainerConfiguration(openFGADevServiceConfig, containerAddress,
                                    devServicesConfigProperties::put);
                            addCredentialsConfiguration(openFGADevServiceConfig, devServicesConfigProperties::put);

                            return DevServicesResultBuildItem.discovered()
                                    .name(FEATURE)
                                    .containerId(containerAddress.getId())
                                    .description("OpenFGA DevServices Services")
                                    .config(devServicesConfigProperties)
                                    .build();
                        })
                        .orElseGet(startSupplier));
    }

    private static String getPropertyWhenResolved(String key, DevServicesOpenFGAConfig devConfig,
            CompletableFuture<Map<String, String>> resolvedConfigProperties) {
        try {
            var configProperties = resolvedConfigProperties.get(devConfig.startupTimeout().toMillis(), TimeUnit.MILLISECONDS);
            return configProperties.get(key);
        } catch (Throwable e) {
            Throwable cause = e;
            if (cause instanceof ExecutionException) {
                cause = cause.getCause();
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }
    }

    private static void addContainerConfigurationResolvers(DevServicesOpenFGAConfig devConfig,
            CompletableFuture<Map<String, String>> resolvedConfigProperties,
            BiConsumer<String, Function<StartableContainer<QuarkusOpenFGAContainer>, String>> add) {

        add.accept(URL_CONFIG_KEY, s -> getPropertyWhenResolved(URL_CONFIG_KEY, devConfig, resolvedConfigProperties));
        add.accept(STORE_ID_CONFIG_KEY, s -> getPropertyWhenResolved(STORE_ID_CONFIG_KEY, devConfig, resolvedConfigProperties));
        loadAuthorizationModelDefinition(devConfig).ifPresent(schema -> add.accept(AUTHORIZATION_MODEL_ID_CONFIG_KEY,
                s -> getPropertyWhenResolved(AUTHORIZATION_MODEL_ID_CONFIG_KEY, devConfig, resolvedConfigProperties)));
    }

    private static void resolveContainerConfiguration(DevServicesOpenFGAConfig devConfig,
            ContainerAddress containerAddress, BiConsumer<String, String> add) {

        withAPI(containerAddress.getHost(), containerAddress.getPort(), devConfig, (instanceURL, api) -> {

            add.accept(URL_CONFIG_KEY, instanceURL.toExternalForm());

            var storeId = findStore(api, devConfig);
            add.accept(STORE_ID_CONFIG_KEY, storeId);

            loadAuthorizationModelDefinition(devConfig)
                    .ifPresent(schema -> {
                        var authModelId = findAuthorizationModel(api, schema, devConfig);
                        add.accept(AUTHORIZATION_MODEL_ID_CONFIG_KEY, authModelId);
                    });
        });
    }

    private static void addCredentialsConfiguration(DevServicesOpenFGAConfig devConfig, BiConsumer<String, String> add) {
        var method = devConfig.authentication().method();
        switch (method) {
            case NONE -> {
            }
            case PRESHARED -> {
                var presharedKey = devConfig.authentication().preshared()
                        .orElseThrow(() -> missingKeyError(AUTHN_PREFIX + "preshared"))
                        .keys().stream().findAny()
                        .orElseThrow(() -> configError("No pre-shared keys", AUTHN_PRESHARED_KEYS_KEY));
                add.accept(CREDS_PRESHARED_KEY_KEY, presharedKey);
            }
            case OIDC -> {
                var oidc = devConfig.authentication().oidc()
                        .orElseThrow(() -> missingKeyError(AUTHN_PREFIX + "oidc"));
                add.accept(CREDS_OIDC_ISSUER_KEY, oidc.issuer());
                add.accept(CREDS_OIDC_AUDIENCE_KEY, oidc.audience());
            }
            default -> {
                log.warn("Unsupported credentials method: %s".formatted(devConfig.authentication().method()));
            }
        }
    }

    private static String createStore(API api, DevServicesOpenFGAConfig config) {
        try {

            log.info("Initializing store '%s'...".formatted(config.storeName()));

            return api
                    .createStore(CreateStoreRequest.builder().name(config.storeName())
                            .build())
                    .await().atMost(config.startupTimeout())
                    .id();

        } catch (Throwable e) {
            throw new RuntimeException("Store initialization failed", e);
        }
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

    private static String findStore(API api, DevServicesOpenFGAConfig config) {
        try {

            var client = new OpenFGAClient(api);

            return client.listAllStores().await()
                    .atMost(config.startupTimeout())
                    .stream().filter(store -> store.getName().equals(config.storeName()))
                    .map(Store::getId)
                    .findFirst()
                    .orElseThrow();

        } catch (Throwable x) {
            throw new ConfigurationException(
                    format("Could not find store '%s' in shared DevServices instance: %s", config.storeName(), x.getMessage()),
                    x);
        }
    }

    private static String findAuthorizationModel(API api, AuthorizationModelSchema schema, DevServicesOpenFGAConfig config) {
        try {
            var storeId = findStore(api, config);

            var client = new AuthorizationModelsClient(api, Uni.createFrom().item(storeId));

            return client.listAll().await()
                    .atMost(config.startupTimeout())
                    .stream().filter(i -> i.getTypeDefinitions().equals(schema.getTypeDefinitions()))
                    .map(AuthorizationModel::getId)
                    .findFirst()
                    .orElseThrow();

        } catch (Throwable x) {
            throw new ConfigurationException(
                    format("Could not find authorization model in shared DevServices instance: %s", x.getMessage()), x);
        }
    }

    private static String loadAuthorizationModel(API api, String storeId, AuthorizationModelSchema schema,
            DevServicesOpenFGAConfig config) {

        try {
            log.info("Initializing authorization model...");

            var request = WriteAuthorizationModelRequest.builder()
                    .schemaVersion(schema.getSchemaVersion())
                    .typeDefinitions(schema.getTypeDefinitions())
                    .conditions(schema.getConditions())
                    .build();

            return api.writeAuthorizationModel(storeId, request)
                    .await()
                    .atMost(config.startupTimeout())
                    .authorizationModelId();

        } catch (Exception e) {
            throw new RuntimeException("Model initialization failed", e);
        }
    }

    private static Optional<Collection<RelTupleKeyed>> loadAuthorizationTuplesDefinition(
            DevServicesOpenFGAConfig devServicesConfig) {
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

    private static void loadAuthorizationTuples(API api, String storeId, String authModelId,
            Collection<RelTupleKeyed> authTuples, DevServicesOpenFGAConfig config) {
        try {
            log.info("Initializing authorization tuples...");

            var writeRequest = WriteRequest.builder()
                    .authorizationModelId(authModelId)
                    .writes(WriteRequest.Writes.of(authTuples))
                    .build();
            api.write(storeId, writeRequest)
                    .await()
                    .atMost(config.startupTimeout());

        } catch (Exception e) {
            throw new RuntimeException("Tuples initialization failed", e);
        }
    }

    private static String readLocation(String location) throws IOException {
        if (location.startsWith(LOC_FILESYSTEM_PREFIX)) {
            var path = Path.of(location.substring(LOC_FILESYSTEM_PREFIX.length()));
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

        String resourceLocation;
        if (location.startsWith(LOC_CLASSPATH_PREFIX)) {
            resourceLocation = location.substring(LOC_CLASSPATH_PREFIX.length());
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

    private static URL getInstanceURL(String host, int port, DevServicesOpenFGAConfig config) {
        try {
            return new URL(config.tls().isPresent() ? "https" : "http", host, port, "");
        } catch (MalformedURLException e) {
            // Should not happen
            throw new RuntimeException(e);
        }
    }

    private static void withAPI(String host, int port, DevServicesOpenFGAConfig devConfig,
            BiConsumer<URL, API> apiConsumer) {

        var instanceURL = getInstanceURL(host, port, devConfig);

        var credentialsProvider = switch (devConfig.authentication().method()) {
            case NONE -> UnauthenticatedCredentialsProvider.INSTANCE;
            case PRESHARED -> new PresharedKeyCredentialsProvider(
                    devConfig.authentication().preshared().orElseThrow(() -> missingKeyError(AUTHN_PREFIX + "preshared"))
                            .keys().stream().findAny()
                            .orElseThrow(() -> configError("No pre-shared keys", AUTHN_PRESHARED_KEYS_KEY)));
            case OIDC -> throw new ConfigurationException(
                    "When using OIDC authentication, store, authorization model and tuples must be pre-configured");
        };

        var vertx = Vertx.vertx();
        try (var api = new API(VertxWebClientFactory.create(instanceURL, vertx), credentialsProvider)) {
            apiConsumer.accept(instanceURL, api);
        } finally {
            vertx.close().await().atMost(devConfig.startupTimeout());
        }
    }

    private static ConfigurationException missingKeyError(String key) {
        return configError("Missing configuration", key);
    }

    private static ConfigurationException configError(String message, String key) {
        return new ConfigurationException(message, Set.of(CONFIG_PREFIX + key));
    }

    private static class QuarkusOpenFGAContainer extends OpenFGAContainer {
        String sharedHostName;
        OptionalInt fixedExposedHttpPort;
        OptionalInt fixedExposedGrpcPort;
        OptionalInt fixedExposedPlaygroundPort;
        boolean tlsEnabled;
        boolean useSharedNetwork;

        public QuarkusOpenFGAContainer(DockerImageName dockerImageName, DevServicesOpenFGAConfig config,
                String defaultNetworkId, boolean useSharedNetwork) {
            super(dockerImageName);
            this.fixedExposedHttpPort = config.httpPort();
            this.fixedExposedGrpcPort = config.grpcPort();
            this.fixedExposedPlaygroundPort = config.playgroundPort();
            this.useSharedNetwork = useSharedNetwork;
            this.sharedHostName = ConfigureUtil.configureNetwork(this, defaultNetworkId, useSharedNetwork, "openfga");

            withStartupTimeout(config.startupTimeout());
            configureCommand(config);
        }

        QuarkusOpenFGAContainer withSharedServiceLabel(LaunchMode launchMode, String serviceLabel, String serviceName) {
            return (QuarkusOpenFGAContainer) ConfigureUtil.configureSharedServiceLabel(this, launchMode, serviceLabel,
                    serviceName);
        }

        private void configureCommand(DevServicesOpenFGAConfig config) {
            var command = new ArrayList<String>();
            command.add("run");

            switch (config.authentication().method()) {
                case NONE -> {
                    log.info("Using no authentication for OpenFGA");
                    command.add("--authn-method=none");
                }
                case PRESHARED -> {
                    log.info("Using pre-shared authentication for OpenFGA");
                    command.add("--authn-method=preshared");
                    var preshared = config.authentication().preshared()
                            .orElseThrow(() -> new ConfigurationException("Missing pre-shared configuration",
                                    Set.of("quarkus.openfga.authn.preshared.keys")));
                    command.add("--authn-preshared-keys=" + String.join(",", preshared.keys()));
                }
                case OIDC -> {
                    log.info("Using OIDC authentication for OpenFGA");
                    command.add("--authn-method=oidc");
                    config.authentication().oidc().ifPresent(oidc -> {
                        command.add("--authn-oidc-issuer=" + oidc.issuer());
                        command.add("--authn-oidc-audience=" + oidc.audience());
                        oidc.issuerAliases()
                                .ifPresent(aliases -> command.add("--authn-oidc-issuer-aliases=" + String.join(",", aliases)));
                        oidc.subjects()
                                .ifPresent(subjects -> command.add("--authn-oidc-subjects=" + String.join(",", subjects)));
                        oidc.clientIdClaims()
                                .ifPresent(claims -> command.add("--authn-oidc-client-id-claims=" + String.join(",", claims)));
                    });
                }
            }
            config.tls().ifPresent(tls -> {
                try {
                    var certPath = tls.pemCertificatePath();
                    var keyPath = tls.pemKeyPath();
                    command.add("--http-tls-enabled=true");
                    command.add("--http-tls-cert=/tls/cert.pem");
                    command.add("--http-tls-key=/tls/key.pem");
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
            });

            setCommand(command.toArray(String[]::new));
        }

        @Override
        protected void configure() {
            super.configure();
            if (useSharedNetwork) {
                return;
            }

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

        @Override
        public String getHost() {
            return useSharedNetwork ? sharedHostName : super.getHost();
        }

        public int getHttpPort() {
            if (useSharedNetwork) {
                return OPEN_FGA_EXPOSED_HTTP_PORT;
            }

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
