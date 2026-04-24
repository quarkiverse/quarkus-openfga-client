package io.quarkiverse.openfga.deployment;

import static io.quarkiverse.openfga.deployment.OpenFGAProcessor.FEATURE;
import static java.lang.String.format;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.openfga.OpenFGAContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkiverse.openfga.client.AuthorizationModelsClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.api.VertxWebClientFactory;
import io.quarkiverse.openfga.client.api.auth.PresharedKeyCredentialsProvider;
import io.quarkiverse.openfga.client.api.auth.UnauthenticatedCredentialsProvider;
import io.quarkiverse.openfga.client.model.AuthorizationModel;
import io.quarkiverse.openfga.client.model.AuthorizationModelSchema;
import io.quarkiverse.openfga.client.model.RelTuple;
import io.quarkiverse.openfga.client.model.RelTupleDefinition;
import io.quarkiverse.openfga.client.model.RelTupleKeyed;
import io.quarkiverse.openfga.client.model.RelTupleKeys;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.dto.CreateStoreRequest;
import io.quarkiverse.openfga.client.model.dto.WriteAuthorizationModelRequest;
import io.quarkiverse.openfga.client.model.dto.WriteRequest;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.IsDevServicesSupportedByLaunchMode;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CuratedApplicationShutdownBuildItem;
import io.quarkus.deployment.builditem.DevServicesComposeProjectBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem.RunningDevService;
import io.quarkus.deployment.builditem.DevServicesSharedNetworkBuildItem;
import io.quarkus.deployment.builditem.DockerStatusBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.dev.devservices.DevServicesConfig;
import io.quarkus.deployment.util.FileUtil;
import io.quarkus.devservices.common.ComposeLocator;
import io.quarkus.devservices.common.ConfigureUtil;
import io.quarkus.devservices.common.ContainerAddress;
import io.quarkus.devservices.common.ContainerLocator;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.runtime.util.ClassPathUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;

@SuppressWarnings("deprecation")
public class DevServicesOpenFGAProcessor {

    private static final Logger log = Logger.getLogger(DevServicesOpenFGAProcessor.class);

    public static final String OPEN_FGA_VERSION = "v1.12.1";
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
    static final String SHARED_KEY_CONFIG_KEY = CONFIG_PREFIX + "shared-key";
    static final String CREDS_PREFIX = CONFIG_PREFIX + "credentials.";
    static final String CREDS_METHOD_KEY = CREDS_PREFIX + "method";
    static final String CREDS_PRESHARED_PREFIX = CREDS_PREFIX + "preshared.";
    static final String CREDS_PRESHARED_KEY_KEY = CREDS_PRESHARED_PREFIX + "key";
    static final String CREDS_OIDC_PREFIX = CREDS_PREFIX + "oidc.";
    static final String CREDS_OIDC_CLIENT_ID_KEY = CREDS_OIDC_PREFIX + "client-id";
    static final String CREDS_OIDC_CLIENT_SECRET_KEY = CREDS_OIDC_PREFIX + "client-secret";
    static final String CREDS_OIDC_ISSUER_KEY = CREDS_OIDC_PREFIX + "issuer";
    static final String CREDS_OIDC_AUDIENCE_KEY = CREDS_OIDC_PREFIX + "audience";
    static final String CREDS_OIDC_SCOPES_KEY = CREDS_OIDC_PREFIX + "scopes";
    static final String CREDS_OIDC_TOKEN_ISSUER_KEY = CREDS_OIDC_PREFIX + "token-issuer";
    static final String CREDS_OIDC_TOKEN_ISSUER_PATH_KEY = CREDS_OIDC_PREFIX + "token-issuer-path";
    static final String CREDS_OIDC_TOKEN_EXPIRATION_THRESHOLD_KEY = CREDS_OIDC_PREFIX + "token-expiration-threshold";
    static final String CREDS_OIDC_TOKEN_EXPIRATION_THRESHOLD_JITTER_KEY = CREDS_OIDC_PREFIX
            + "token-expiration-threshold-jitter";
    static final String DEVSERVICES_PREFIX = "quarkus.devservices.";
    static final String AUTHN_PREFIX = DEVSERVICES_PREFIX + "openfga.";
    static final String AUTHN_PRESHARED_PREFIX = AUTHN_PREFIX + "authn.preshared.";
    static final String AUTHN_PRESHARED_KEYS_KEY = AUTHN_PRESHARED_PREFIX + "keys";
    static final String LOC_CLASSPATH_PREFIX = "classpath:";
    static final String LOC_FILESYSTEM_PREFIX = "filesystem:";
    static final String TEST_LAUNCH_MODE = "io.quarkiverse.openfga.test-launch-mode";

    static final ContainerLocator openFGAContainerLocator = ContainerLocator
            .locateContainerWithLabels(OPEN_FGA_EXPOSED_HTTP_PORT, DEV_SERVICE_LABEL);
    private static volatile RunningDevService devService;
    private static volatile ServiceIdentity capturedServiceIdentity;
    private static volatile boolean closeTaskRegistered;

    @BuildStep(onlyIf = { IsDevServicesSupportedByLaunchMode.class, DevServicesConfig.Enabled.class })
    public void startContainers(OpenFGABuildTimeConfig config,
            LaunchModeBuildItem launchModeBuildItem,
            DockerStatusBuildItem dockerStatusBuildItem,
            CuratedApplicationShutdownBuildItem closeBuildItem,
            List<DevServicesSharedNetworkBuildItem> sharedNetworkBuildItem,
            DevServicesComposeProjectBuildItem composeProjectBuildItem,
            DevServicesConfig devServicesConfig,
            BuildProducer<DevServicesResultBuildItem> devServicesResults) {

        final ClassLoader resourceClassLoader = Thread.currentThread().getContextClassLoader();

        var launchMode = Optional.ofNullable(System.getProperty(TEST_LAUNCH_MODE))
                .flatMap(mode -> Arrays.stream(LaunchMode.values())
                        .filter(m -> m.getDefaultProfile().equals(mode)).findFirst())
                .orElse(launchModeBuildItem.getLaunchMode());

        DevServicesOpenFGAConfig openFGADevServiceConfig = config.devservices();

        if (!openFGADevServiceConfig.enabled().orElse(true)) {
            closeCachedDevServiceState();
            log.debug("Not starting devservices for OpenFGA as it has been disabled in the config");
            return;
        }

        var useSharedNetwork = DevServicesSharedNetworkBuildItem.isSharedNetworkRequired(devServicesConfig,
                sharedNetworkBuildItem);

        if (ConfigUtils.isPropertyNonEmpty(URL_CONFIG_KEY)) {
            closeCachedDevServiceState();
            log.debug("Not starting devservices for default OpenFGA client as url has been provided");
            return;
        }

        if (!dockerStatusBuildItem.isContainerRuntimeAvailable()) {
            log.warn("Requires configuration of '" + URL_CONFIG_KEY + "' or a working docker instance");
            return;
        }

        final var imageName = openFGADevServiceConfig.imageName().orElse(OPEN_FGA_IMAGE);
        final var dockerImageName = DockerImageName.parse(imageName).asCompatibleSubstituteFor(OPEN_FGA_IMAGE);
        final var initializationSpec = loadInitializationSpec(openFGADevServiceConfig, resourceClassLoader);
        final var serviceIdentity = buildServiceIdentity(openFGADevServiceConfig, imageName, initializationSpec);
        final var credentialsConfig = resolveCredentialsConfiguration(openFGADevServiceConfig);
        if (devService != null) {
            if (Objects.equals(capturedServiceIdentity, serviceIdentity)) {
                devServicesResults.produce(devService.toBuildItem());
                return;
            }
            closeCachedDevServiceState();
        }

        devService = openFGAContainerLocator
                .locateContainer(openFGADevServiceConfig.serviceName(), openFGADevServiceConfig.shared(), launchMode)
                .or(() -> ComposeLocator.locateContainer(composeProjectBuildItem,
                        List.of(openFGADevServiceConfig.imageName().orElse(OPEN_FGA_IMAGE_NAME)),
                        OPEN_FGA_EXPOSED_HTTP_PORT, launchMode, useSharedNetwork))
                .map(containerAddress -> {
                    var discovered = resolveDiscoveredState(openFGADevServiceConfig, containerAddress, initializationSpec);
                    warnIfSharedInitializationDrift(openFGADevServiceConfig, initializationSpec, containerAddress, discovered);

                    var discoveredConfig = new HashMap<>(discovered.config());
                    discoveredConfig.putAll(credentialsConfig);

                    return new RunningDevService(FEATURE, "OpenFGA DevServices Instance", containerAddress.getId(), null,
                            Map.copyOf(discoveredConfig));
                })
                .orElseGet(() -> startOwnedService(openFGADevServiceConfig, dockerImageName, composeProjectBuildItem,
                        useSharedNetwork, resourceClassLoader, launchMode, initializationSpec, credentialsConfig));

        capturedServiceIdentity = serviceIdentity;
        devServicesResults.produce(devService.toBuildItem());

        if (!closeTaskRegistered) {
            closeTaskRegistered = true;
            closeBuildItem.addCloseTask(() -> {
                try {
                    closeCachedDevServiceState();
                } finally {
                    closeTaskRegistered = false;
                }
            }, true);
        }
    }

    private static RunningDevService startOwnedService(DevServicesOpenFGAConfig devConfig,
            DockerImageName dockerImageName,
            DevServicesComposeProjectBuildItem composeProjectBuildItem,
            boolean useSharedNetwork,
            ClassLoader resourceClassLoader,
            LaunchMode launchMode,
            InitializationSpec initializationSpec,
            Map<String, String> credentialsConfig) {
        var container = new QuarkusOpenFGAContainer(dockerImageName, devConfig,
                composeProjectBuildItem.getDefaultNetworkId(), useSharedNetwork, resourceClassLoader);
        if (devConfig.shared()) {
            container = container.withSharedServiceLabel(launchMode, DEV_SERVICE_LABEL, devConfig.serviceName());
        }

        return startOwnedService(container, credentialsConfig,
                startedContainer -> initializeContainerConfiguration(startedContainer, devConfig, initializationSpec));
    }

    static RunningDevService startOwnedService(QuarkusOpenFGAContainer container,
            Map<String, String> credentialsConfig,
            ContainerConfigurationInitializer configurationInitializer) {
        try {
            container.start();

            var config = new HashMap<>(configurationInitializer.initialize(container));
            config.putAll(credentialsConfig);

            return new RunningDevService(FEATURE, "OpenFGA DevServices Instance", container.getContainerId(),
                    container::close, Map.copyOf(config));
        } catch (RuntimeException | Error startupFailure) {
            try {
                container.close();
            } catch (RuntimeException closeFailure) {
                startupFailure.addSuppressed(closeFailure);
                log.warn("Failed to stop OpenFGA DevServices instance after startup failure", closeFailure);
            }
            throw startupFailure;
        }
    }

    private static Map<String, String> initializeContainerConfiguration(QuarkusOpenFGAContainer container,
            DevServicesOpenFGAConfig devConfig,
            InitializationSpec initializationSpec) {
        var configProperties = new HashMap<String, String>();

        withAPI(container.getHost(), container.getHttpPort(), devConfig, (instanceURL, api) -> {
            configProperties.put(URL_CONFIG_KEY, instanceURL.toExternalForm());

            var storeId = createStore(api, devConfig);
            configProperties.put(STORE_ID_CONFIG_KEY, storeId);

            initializationSpec.authorizationModel()
                    .ifPresentOrElse(loadedModel -> {
                        var authorizationModelId = loadAuthorizationModel(api, storeId, loadedModel.schema(), devConfig);
                        configProperties.put(AUTHORIZATION_MODEL_ID_CONFIG_KEY, authorizationModelId);

                        initializationSpec.authorizationTuples()
                                .ifPresent(loadedTuples -> loadAuthorizationTuples(api, storeId, authorizationModelId,
                                        loadedTuples.tuples(), devConfig));
                    }, () -> {
                        if (initializationSpec.authorizationTuples().isPresent()) {
                            log.warn("No authorization model configured, no tuples will be initialized");
                        }
                    });
        });

        return Map.copyOf(configProperties);
    }

    static void closeCachedDevServiceState() {
        try {
            if (devService != null) {
                devService.close();
            }
        } catch (IOException e) {
            log.warn("Failed to stop previous OpenFGA DevServices instance", e);
        } finally {
            devService = null;
            capturedServiceIdentity = null;
        }
    }

    static void cacheDevServiceState(RunningDevService service, ServiceIdentity serviceIdentity) {
        devService = service;
        capturedServiceIdentity = serviceIdentity;
    }

    static RunningDevService cachedDevService() {
        return devService;
    }

    static ServiceIdentity cachedServiceIdentity() {
        return capturedServiceIdentity;
    }

    static void resetLifecycleStateForTest() {
        closeCachedDevServiceState();
        closeTaskRegistered = false;
    }

    private static DiscoveredState resolveDiscoveredState(DevServicesOpenFGAConfig devConfig,
            ContainerAddress containerAddress,
            InitializationSpec initializationSpec) {

        var configProperties = new HashMap<String, String>();
        @SuppressWarnings("unchecked")
        Optional<String>[] detectedModelFingerprint = new Optional[] { Optional.empty() };
        boolean[] tuplesDrift = new boolean[] { false };

        withAPI(containerAddress.getHost(), containerAddress.getPort(), devConfig, (instanceURL, api) -> {
            configProperties.put(URL_CONFIG_KEY, instanceURL.toExternalForm());

            var storeId = findStore(api, devConfig);
            configProperties.put(STORE_ID_CONFIG_KEY, storeId);

            Optional<String> authorizationModelId = Optional.empty();
            if (initializationSpec.authorizationModel().isPresent()) {
                var requestedModel = initializationSpec.authorizationModel().orElseThrow();
                var model = findAuthorizationModel(api, requestedModel.schema(), devConfig);
                configProperties.put(AUTHORIZATION_MODEL_ID_CONFIG_KEY, model.getId());
                authorizationModelId = Optional.of(model.getId());
                detectedModelFingerprint[0] = Optional.of(requestedModel.fingerprint());
            }

            if (initializationSpec.authorizationTuples().isPresent()) {
                if (authorizationModelId.isPresent()) {
                    var requestedTuples = initializationSpec.authorizationTuples().orElseThrow().tuples();
                    tuplesDrift[0] = !hasAllRequestedTuples(api, storeId, requestedTuples, devConfig);
                } else {
                    log.warn(
                            "Authorization tuples were configured, but no authorization model was configured. Tuples are not initialized for discovered shared OpenFGA DevServices instances.");
                }
            }
        });

        return new DiscoveredState(Map.copyOf(configProperties), detectedModelFingerprint[0], tuplesDrift[0]);
    }

    private static void warnIfSharedInitializationDrift(DevServicesOpenFGAConfig devConfig,
            InitializationSpec initializationSpec,
            ContainerAddress containerAddress,
            DiscoveredState discovered) {
        if (!devConfig.shared()) {
            return;
        }

        var reasons = sharedInitializationDriftReasons(
                initializationSpec.authorizationModel().map(LoadedAuthorizationModel::fingerprint),
                initializationSpec.authorizationTuples().map(LoadedAuthorizationTuples::fingerprint),
                discovered.detectedModelFingerprint(),
                discovered.tuplesDrift());

        if (reasons.isEmpty()) {
            return;
        }

        log.warnf(
                "Shared OpenFGA DevServices container '%s' was discovered, but initialization arguments differ. "
                        + "Discovered containers are not re-initialized. Differences: %s",
                containerAddress.getId(),
                String.join("; ", reasons));
    }

    static List<String> sharedInitializationDriftReasons(Optional<String> requestedModelFingerprint,
            Optional<String> requestedTuplesFingerprint,
            Optional<String> detectedModelFingerprint,
            boolean tuplesDrift) {
        var reasons = new ArrayList<String>();

        if (requestedModelFingerprint.isPresent()) {
            var requested = requestedModelFingerprint.orElseThrow();
            var detected = detectedModelFingerprint.orElse("unavailable");
            if (!Objects.equals(requested, detected)) {
                reasons.add("authorization model fingerprint requested=" + requested + ", detected=" + detected);
            }
        }

        if (requestedTuplesFingerprint.isPresent() && tuplesDrift) {
            reasons.add("authorization tuples do not fully match the requested initialization set"
                    + " (requested fingerprint=" + requestedTuplesFingerprint.orElseThrow() + ")");
        }

        return List.copyOf(reasons);
    }

    private static boolean hasAllRequestedTuples(API api, String storeId,
            Collection<RelTupleKeyed> requestedTuples,
            DevServicesOpenFGAConfig config) {
        try {
            var existing = new StoreClient(api, Uni.createFrom().item(storeId)).readAllTuples()
                    .await().atMost(config.startupTimeout())
                    .stream()
                    .map(RelTuple::getKey)
                    .collect(Collectors.toSet());

            for (var requested : requestedTuples) {
                RelTupleDefinition requestedDefinition = requested.conditional();
                if (!existing.contains(requestedDefinition)) {
                    return false;
                }
            }
            return true;
        } catch (Throwable error) {
            log.warnf(error,
                    "Failed to verify authorization tuples for discovered OpenFGA DevServices instance. Assuming tuple drift.");
            return false;
        }
    }

    private static Map<String, String> resolveCredentialsConfiguration(DevServicesOpenFGAConfig devConfig) {
        var credentials = new HashMap<String, String>();
        addCredentialsConfiguration(devConfig, credentials::put);
        return Map.copyOf(credentials);
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
            default -> log.warn("Unsupported credentials method: %s".formatted(devConfig.authentication().method()));
        }
    }

    private static InitializationSpec loadInitializationSpec(DevServicesOpenFGAConfig config, ClassLoader resourceClassLoader) {
        return new InitializationSpec(
                config.storeName(),
                loadAuthorizationModelDefinition(config, resourceClassLoader),
                loadAuthorizationTuplesDefinition(config, resourceClassLoader));
    }

    private static Optional<LoadedAuthorizationModel> loadAuthorizationModelDefinition(
            DevServicesOpenFGAConfig devServicesConfig,
            ClassLoader resourceClassLoader) {
        return devServicesConfig.authorizationModel()
                .or(() -> devServicesConfig.authorizationModelLocation()
                        .map(location -> {
                            try {
                                return readLocation(location, resourceClassLoader);
                            } catch (Throwable x) {
                                throw new RuntimeException(
                                        format("Unable to load authorization model from '%s'", location), x);
                            }
                        }))
                .map(authModelJSON -> {
                    try {
                        var schema = AuthorizationModelSchema.parse(authModelJSON);
                        return new LoadedAuthorizationModel(
                                schema,
                                fingerprintAuthorizationModel(schema));
                    } catch (Throwable t) {
                        throw new RuntimeException("Unable to parse authorization model", t);
                    }
                });
    }

    private static Optional<LoadedAuthorizationTuples> loadAuthorizationTuplesDefinition(
            DevServicesOpenFGAConfig devServicesConfig,
            ClassLoader resourceClassLoader) {
        return devServicesConfig.authorizationTuples()
                .or(() -> devServicesConfig.authorizationTuplesLocation()
                        .map(location -> {
                            try {
                                return readLocation(location, resourceClassLoader);
                            } catch (Throwable x) {
                                throw new RuntimeException(
                                        format("Unable to load authorization tuples from '%s'", location), x);
                            }
                        }))
                .map(authTuplesJSON -> {
                    try {
                        return new LoadedAuthorizationTuples(
                                List.copyOf(RelTupleKeys.parseList(authTuplesJSON).getTupleKeys()),
                                fingerprint(authTuplesJSON));
                    } catch (Throwable t) {
                        throw new RuntimeException("Unable to parse authorization tuples", t);
                    }
                });
    }

    static ServiceIdentity buildServiceIdentity(DevServicesOpenFGAConfig devConfig,
            String imageName,
            InitializationSpec initializationSpec) {
        return buildServiceIdentity(devConfig, imageName, initializationSpec,
                runtimeClientCredentialsIdentity(key -> ConfigProvider.getConfig().getOptionalValue(key, String.class)));
    }

    static ServiceIdentity buildServiceIdentity(DevServicesOpenFGAConfig devConfig,
            String imageName,
            InitializationSpec initializationSpec,
            RuntimeClientCredentialsIdentity runtimeClientCredentialsIdentity) {
        return new ServiceIdentity(
                imageName,
                devConfig.shared(),
                devConfig.serviceName(),
                devConfig.httpPort(),
                devConfig.grpcPort(),
                devConfig.playgroundPort(),
                devConfig.storeName(),
                devConfig.startupTimeout(),
                authenticationIdentity(devConfig.authentication()),
                devConfig.tls().map(tls -> new TlsIdentity(tls.pemCertificatePath(), tls.pemKeyPath())),
                Collections.unmodifiableMap(new TreeMap<>(devConfig.containerEnv())),
                devConfig.reuse(),
                initializationSpec.authorizationModel().map(LoadedAuthorizationModel::fingerprint),
                initializationSpec.authorizationTuples().map(LoadedAuthorizationTuples::fingerprint),
                runtimeClientCredentialsIdentity);
    }

    static RuntimeClientCredentialsIdentity runtimeClientCredentialsIdentity(
            Function<String, Optional<String>> propertyResolver) {
        var clientId = propertyResolver.apply(CREDS_OIDC_CLIENT_ID_KEY);
        var clientSecret = propertyResolver.apply(CREDS_OIDC_CLIENT_SECRET_KEY);
        var audience = propertyResolver.apply(CREDS_OIDC_AUDIENCE_KEY);
        var scopes = propertyResolver.apply(CREDS_OIDC_SCOPES_KEY);
        var tokenIssuer = propertyResolver.apply(CREDS_OIDC_TOKEN_ISSUER_KEY);
        var tokenIssuerPath = propertyResolver.apply(CREDS_OIDC_TOKEN_ISSUER_PATH_KEY);
        var tokenExpirationThreshold = propertyResolver.apply(CREDS_OIDC_TOKEN_EXPIRATION_THRESHOLD_KEY);
        var tokenExpirationThresholdJitter = propertyResolver.apply(CREDS_OIDC_TOKEN_EXPIRATION_THRESHOLD_JITTER_KEY);
        var runtimeOidcIdentity = Optional.<RuntimeOidcIdentity> empty();
        if (clientId.isPresent()
                || clientSecret.isPresent()
                || audience.isPresent()
                || scopes.isPresent()
                || tokenIssuer.isPresent()
                || tokenIssuerPath.isPresent()
                || tokenExpirationThreshold.isPresent()
                || tokenExpirationThresholdJitter.isPresent()) {
            runtimeOidcIdentity = Optional.of(new RuntimeOidcIdentity(
                    clientId,
                    clientSecret,
                    audience,
                    scopes,
                    tokenIssuer,
                    tokenIssuerPath,
                    tokenExpirationThreshold,
                    tokenExpirationThresholdJitter));
        }
        return new RuntimeClientCredentialsIdentity(
                propertyResolver.apply(SHARED_KEY_CONFIG_KEY),
                propertyResolver.apply(CREDS_METHOD_KEY),
                propertyResolver.apply(CREDS_PRESHARED_KEY_KEY),
                runtimeOidcIdentity);
    }

    private static AuthenticationIdentity authenticationIdentity(DevServicesOpenFGAConfig.Authentication auth) {
        var presharedKeys = auth.preshared()
                .map(DevServicesOpenFGAConfig.Authentication.Preshared::keys)
                .map(list -> list.stream().sorted().toList())
                .orElseGet(List::of);

        var oidc = auth.oidc().map(cfg -> new OidcIdentity(
                cfg.issuer(),
                cfg.audience(),
                cfg.issuerAliases().map(list -> list.stream().sorted().toList()).orElseGet(List::of),
                cfg.subjects().map(list -> list.stream().sorted().toList()).orElseGet(List::of),
                cfg.clientIdClaims().map(list -> list.stream().sorted().toList()).orElseGet(List::of)));

        return new AuthenticationIdentity(auth.method(), presharedKeys, oidc);
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

    private static AuthorizationModel findAuthorizationModel(API api, AuthorizationModelSchema schema,
            DevServicesOpenFGAConfig config) {
        try {
            var storeId = findStore(api, config);
            var client = new AuthorizationModelsClient(api, Uni.createFrom().item(storeId));

            return client.listAll().await()
                    .atMost(config.startupTimeout())
                    .stream()
                    .filter(model -> Objects.equals(model.getSchemaVersion(), schema.getSchemaVersion()))
                    .filter(model -> Objects.equals(model.getTypeDefinitions(), schema.getTypeDefinitions()))
                    .filter(model -> Objects.equals(model.getConditions(), schema.getConditions()))
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

    private static String readLocation(String location, ClassLoader resourceClassLoader) throws IOException {
        if (location.startsWith(LOC_FILESYSTEM_PREFIX)) {
            var path = Path.of(location.substring(LOC_FILESYSTEM_PREFIX.length()));
            return Files.readString(path);
        }

        URL resourceURL = getLocationResource(location, resourceClassLoader);
        return ClassPathUtils.readStream(resourceURL, (stream) -> {
            try {
                var contents = FileUtil.readFileContents(stream);
                return new String(contents, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read resource: " + location, e);
            }
        });
    }

    private static Path resolvePath(String location, ClassLoader resourceClassLoader) throws IOException {
        if (location.startsWith(LOC_FILESYSTEM_PREFIX)) {
            return Path.of(location.substring(LOC_FILESYSTEM_PREFIX.length()));
        }

        URL resourceURL = getLocationResource(location, resourceClassLoader);
        return ClassPathUtils.toLocalPath(resourceURL);
    }

    private static URL getLocationResource(String location, ClassLoader resourceClassLoader) throws IOException {

        String resourceLocation;
        if (location.startsWith(LOC_CLASSPATH_PREFIX)) {
            resourceLocation = location.substring(LOC_CLASSPATH_PREFIX.length());
        } else {
            resourceLocation = location;
        }

        URL resourceURL = resourceClassLoader != null ? resourceClassLoader.getResource(resourceLocation) : null;
        if (resourceURL == null) {
            resourceURL = Thread.currentThread().getContextClassLoader().getResource(resourceLocation);
            if (resourceURL == null) {
                resourceURL = QuarkusClassLoader.getSystemResource(resourceLocation);
            }
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
        var originalCl = Thread.currentThread().getContextClassLoader();
        var mutinyCl = io.smallrye.mutiny.infrastructure.Infrastructure.class.getClassLoader();
        if (mutinyCl != null && mutinyCl != originalCl) {
            Thread.currentThread().setContextClassLoader(mutinyCl);
        }
        try (var api = new API(VertxWebClientFactory.create(instanceURL, vertx), credentialsProvider)) {
            apiConsumer.accept(instanceURL, api);
        } finally {
            try {
                vertx.close().await().atMost(devConfig.startupTimeout());
            } finally {
                if (mutinyCl != null && mutinyCl != originalCl) {
                    Thread.currentThread().setContextClassLoader(originalCl);
                }
            }
        }
    }

    private static String fingerprint(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 digest unavailable", e);
        }
    }

    private static String fingerprintAuthorizationModel(AuthorizationModel model) {
        String raw = model.getSchemaVersion() + "|" + model.getTypeDefinitions() + "|" + model.getConditions();
        return fingerprint(raw);
    }

    private static String fingerprintAuthorizationModel(AuthorizationModelSchema schema) {
        String raw = schema.getSchemaVersion() + "|" + schema.getTypeDefinitions() + "|" + schema.getConditions();
        return fingerprint(raw);
    }

    private static ConfigurationException missingKeyError(String key) {
        return configError("Missing configuration", key);
    }

    private static ConfigurationException configError(String message, String key) {
        String fullKey = key.startsWith("quarkus.") ? key : CONFIG_PREFIX + key;
        return new ConfigurationException(message, Set.of(fullKey));
    }

    static record LoadedAuthorizationModel(AuthorizationModelSchema schema, String fingerprint) {
    }

    static record LoadedAuthorizationTuples(Collection<RelTupleKeyed> tuples, String fingerprint) {
    }

    static record InitializationSpec(String storeName,
            Optional<LoadedAuthorizationModel> authorizationModel,
            Optional<LoadedAuthorizationTuples> authorizationTuples) {
    }

    static record OidcIdentity(String issuer, String audience, List<String> issuerAliases, List<String> subjects,
            List<String> clientIdClaims) {
    }

    static record AuthenticationIdentity(DevServicesOpenFGAConfig.Authentication.Method method,
            List<String> presharedKeys,
            Optional<OidcIdentity> oidc) {
    }

    static record TlsIdentity(String pemCertificatePath, String pemKeyPath) {
    }

    static record ServiceIdentity(String imageName,
            boolean shared,
            String serviceName,
            OptionalInt httpPort,
            OptionalInt grpcPort,
            OptionalInt playgroundPort,
            String storeName,
            Duration startupTimeout,
            AuthenticationIdentity authentication,
            Optional<TlsIdentity> tls,
            Map<String, String> containerEnv,
            boolean reuse,
            Optional<String> authorizationModelFingerprint,
            Optional<String> authorizationTuplesFingerprint,
            RuntimeClientCredentialsIdentity runtimeClientCredentials) {
    }

    static record RuntimeClientCredentialsIdentity(Optional<String> deprecatedSharedKey,
            Optional<String> method,
            Optional<String> presharedKey,
            Optional<RuntimeOidcIdentity> oidc) {
    }

    static record RuntimeOidcIdentity(Optional<String> clientId,
            Optional<String> clientSecret,
            Optional<String> audience,
            Optional<String> scopes,
            Optional<String> tokenIssuer,
            Optional<String> tokenIssuerPath,
            Optional<String> tokenExpirationThreshold,
            Optional<String> tokenExpirationThresholdJitter) {
    }

    private record DiscoveredState(Map<String, String> config,
            Optional<String> detectedModelFingerprint,
            boolean tuplesDrift) {
    }

    @FunctionalInterface
    interface ContainerConfigurationInitializer {
        Map<String, String> initialize(QuarkusOpenFGAContainer container);
    }

    static class QuarkusOpenFGAContainer extends OpenFGAContainer {
        String sharedHostName;
        OptionalInt fixedExposedHttpPort;
        OptionalInt fixedExposedGrpcPort;
        OptionalInt fixedExposedPlaygroundPort;
        boolean tlsEnabled;
        boolean useSharedNetwork;
        ClassLoader resourceClassLoader;

        public QuarkusOpenFGAContainer(DockerImageName dockerImageName, DevServicesOpenFGAConfig config,
                String defaultNetworkId, boolean useSharedNetwork, ClassLoader resourceClassLoader) {
            super(dockerImageName);
            this.waitStrategy.withStartupTimeout(config.startupTimeout());
            this.fixedExposedHttpPort = config.httpPort();
            this.fixedExposedGrpcPort = config.grpcPort();
            this.fixedExposedPlaygroundPort = config.playgroundPort();
            this.useSharedNetwork = useSharedNetwork;
            this.resourceClassLoader = resourceClassLoader;
            this.sharedHostName = ConfigureUtil.configureNetwork(this, defaultNetworkId, useSharedNetwork, "openfga");

            withEnv(config.containerEnv());
            withReuse(config.reuse());
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
                    withFileSystemBind(resolvePath(certPath, resourceClassLoader).toAbsolutePath().toString(),
                            "/tls/cert.pem", BindMode.READ_ONLY);
                    withFileSystemBind(resolvePath(keyPath, resourceClassLoader).toAbsolutePath().toString(),
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
            return (tlsEnabled ? "https" : "http") + "://" + this.getHost() + ":" + this.getHttpPort();
        }
    }
}
