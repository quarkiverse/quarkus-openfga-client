package io.quarkiverse.openfga.deployment;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.*;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem.RunningDevService;
import io.quarkus.deployment.console.ConsoleInstalledBuildItem;
import io.quarkus.deployment.console.StartupLogCompressor;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import io.quarkus.deployment.logging.LoggingSetupBuildItem;
import io.quarkus.devservices.common.ContainerLocator;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.runtime.util.ClassPathUtils;

public class DevServicesOpenFGAProcessor {

    private static final Logger log = Logger.getLogger(DevServicesOpenFGAProcessor.class);
    static final String OPEN_FGA_VERSION = "v0.2.1";
    static final String OPEN_FGA_IMAGE = "openfga/openfga:" + OPEN_FGA_VERSION;
    static final int OPEN_FGA_EXPOSED_PORT = 8080;
    static final String DEV_SERVICE_LABEL = "quarkus-dev-service-openfga";
    static final String CONFIG_PREFIX = "quarkus.openfga.";
    static final String URL_CONFIG_KEY = CONFIG_PREFIX + "url";
    static final String STORE_ID_CONFIG_KEY = CONFIG_PREFIX + "store-id";
    static final String AUTHORIZATION_MODEL_ID_CONFIG_KEY = CONFIG_PREFIX + "authorization-model-id";
    static final ContainerLocator openFGAContainerLocator = new ContainerLocator(DEV_SERVICE_LABEL,
            OPEN_FGA_EXPOSED_PORT);
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
            DevServicesOpenFGAConfig devServicesConfig,
            LaunchModeBuildItem launchMode, Optional<Duration> timeout) {
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

            QuarkusOpenFGAContainer container = new QuarkusOpenFGAContainer(dockerImageName, devServicesConfig.port,
                    devServicesConfig.serviceName)
                    .withNetwork(Network.SHARED)
                    .waitingFor(Wait.forHttp("/stores"));

            timeout.ifPresent(container::withStartupTimeout);

            log.info("Starting OpenFGA...");

            container.start();

            var instanceURL = format("http://%s:%d", container.getHost(), container.getPort());

            var devServicesConfigProperties = new HashMap<String, String>();
            devServicesConfigProperties.put(URL_CONFIG_KEY, instanceURL);

            var storeInitializer = new DevServicesStoreInitializer(instanceURL);

            String storeId;
            try {
                log.info("Initializing authorization store...");

                storeId = storeInitializer.createStore(devServicesConfig.storeName);

                devServicesConfigProperties.put(STORE_ID_CONFIG_KEY, storeId);

            } catch (Exception e) {
                throw new RuntimeException("Store initialization failed", e);
            }

            devServicesConfig.authorizationModel
                    .ifPresentOrElse(authModel -> {
                        try {
                            log.info("Initializing authorization model...");

                            var authorizationModelId = storeInitializer.createAuthorizationModel(storeId, authModel);

                            devServicesConfigProperties.put(AUTHORIZATION_MODEL_ID_CONFIG_KEY, authorizationModelId);

                        } catch (Exception e) {
                            throw new RuntimeException("Model initialization failed", e);
                        }
                    }, () -> devServicesConfig.authorizationModelLocation
                            .ifPresentOrElse(location -> {
                                try {
                                    log.infof("Initializing authorization model from %s...", location);

                                    var modelPath = resolveModelPath(location);

                                    try (var modelStream = new FileInputStream(modelPath.toFile())) {

                                        var authModel = new String(modelStream.readAllBytes(), UTF_8);

                                        var authorizationModelId = storeInitializer.createAuthorizationModel(storeId,
                                                authModel);

                                        devServicesConfigProperties.put(AUTHORIZATION_MODEL_ID_CONFIG_KEY,
                                                authorizationModelId);
                                    }

                                } catch (Exception e) {
                                    throw new RuntimeException("Model initialization failed", e);
                                }
                            }, () -> log.info(
                                    "No authentication model provided, skipping authorization store & model initialization")));

            return new RunningDevService(OpenFGAProcessor.FEATURE, container.getContainerId(), container::close,
                    devServicesConfigProperties);
        };

        return openFGAContainerLocator
                .locateContainer(devServicesConfig.serviceName, devServicesConfig.shared, launchMode.getLaunchMode())
                .map(containerAddress -> {

                    var instanceURL = format("http://%s:%d", containerAddress.getHost(), containerAddress.getPort());

                    String storeId;
                    try {
                        storeId = new DevServicesStoreInitializer(instanceURL)
                                .findStoreId(devServicesConfig.storeName)
                                .orElseThrow(() -> new ConfigurationException(
                                        format("Could not find store '%s' in shared DevServices instance",
                                                devServicesConfig.storeName)));
                    } catch (Throwable t) {
                        throw new RuntimeException("Unable to connect to shared DevServices instance", t);
                    }

                    var config = Map.of(URL_CONFIG_KEY, instanceURL, STORE_ID_CONFIG_KEY, storeId);

                    return new RunningDevService(OpenFGAProcessor.FEATURE, containerAddress.getId(), null, config);
                })
                .orElseGet(defaultOpenFGAInstanceSupplier);
    }

    private static class QuarkusOpenFGAContainer extends GenericContainer<QuarkusOpenFGAContainer> {
        OptionalInt fixedExposedPort;

        public QuarkusOpenFGAContainer(DockerImageName dockerImageName, OptionalInt fixedExposedPort, String serviceName) {
            super(dockerImageName);
            this.fixedExposedPort = fixedExposedPort;
            withCommand("run");
            withNetwork(Network.SHARED);
            if (serviceName != null) { // Only adds the label in dev mode.
                withLabel(DEV_SERVICE_LABEL, serviceName);
            }
        }

        @Override
        protected void configure() {
            super.configure();
            if (fixedExposedPort.isPresent()) {
                addFixedExposedPort(fixedExposedPort.getAsInt(), OPEN_FGA_EXPOSED_PORT);
            } else {
                addExposedPort(OPEN_FGA_EXPOSED_PORT);
            }
        }

        public int getPort() {
            if (fixedExposedPort.isPresent()) {
                return fixedExposedPort.getAsInt();
            }
            return super.getMappedPort(OPEN_FGA_EXPOSED_PORT);
        }
    }

    private Path resolveModelPath(String location) throws IOException {
        location = normalizeLocation(location);
        if (location.startsWith("filesystem:")) {
            return Path.of(location.substring("filesystem:".length()));
        }

        var classpathPath = new AtomicReference<Path>();
        ClassPathUtils.consumeAsPaths(Thread.currentThread().getContextClassLoader(), location, classpathPath::set);

        return classpathPath.get();
    }

    private String normalizeLocation(String location) {
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
}
