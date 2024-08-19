package io.quarkiverse.openfga.deployment;

import java.util.Optional;
import java.util.OptionalInt;

import io.smallrye.config.WithDefault;

public interface DevServicesOpenFGAConfig {

    /**
     * If DevServices has been explicitly enabled or disabled. DevServices is generally enabled
     * by default, unless there is an existing configuration present.
     * <p>
     * When DevServices is enabled Quarkus will attempt to automatically configure and start
     * a database when running in 'dev' or 'test' mode.
     */
    Optional<Boolean> enabled();

    /**
     * The container image name to use, for container based DevServices providers.
     */
    Optional<String> imageName();

    /**
     * Indicates if the OpenFGA instance managed by Quarkus DevServices is shared.
     * When shared, Quarkus looks for running containers using label-based service discovery.
     * If a matching container is found, it is used, and so a second one is not started.
     * Otherwise, DevServices for OpenFGA starts a new container.
     * <p>
     * The discovery uses the {@code quarkus-dev-service-openfga} label.
     * The value is configured using the {@code service-name} property.
     * <p>
     * Container sharing is only used in 'dev' mode.
     */
    @WithDefault("true")
    boolean shared();

    /**
     * The value of the {@code quarkus-dev-service-openfga} label attached to the started container.
     * This property is used when {@code shared} is set to {@code true}.
     * In this case, before starting a container, DevServices for OpenFGA looks for a container with the
     * {@code quarkus-dev-service-openfga} label
     * set to the configured value. If found, it will use this container instead of starting a new one, otherwise it
     * starts a new container with the {@code quarkus-dev-service-openfga} label set to the specified value.
     * <p>
     * This property is used when you need multiple shared OpenFGA instances.
     */
    @WithDefault("openfga")
    String serviceName();

    /**
     * Optional fixed port the HTTP service will be bound to.
     * <p>
     * If not defined, the port will be chosen randomly.
     */
    OptionalInt httpPort();

    /**
     * Optional fixed port the gRPC service will be bound to.
     * <p>
     * If not defined, the port will be chosen randomly.
     */
    OptionalInt grpcPort();

    /**
     * Optional fixed port the Playground service will be bound to.
     * <p>
     * If not defined, the port will be chosen randomly.
     */
    OptionalInt playgroundPort();

    /**
     * Name of authorization store to create for DevServices.
     * <p>
     * Defaults to "dev".
     */
    @WithDefault("dev")
    String storeName();

    /**
     * JSON formatted authorization model to upload during DevServices initialization.
     */
    Optional<String> authorizationModel();

    /**
     * Location of JSON formatted authorization model file to upload during DevServices initialization.
     * <p>
     * The location can be prefixed with {@code classpath:} or {@code filesystem:} to specify where the file
     * will be read from; if not prefixed, it will be read from the classpath.
     */
    Optional<String> authorizationModelLocation();

    /**
     * JSON formatted authorization tuples to upload during DevServices initialization.
     * <p>
     *
     * @implNote Initialization of tuples will only happen if an authorization model is also defined via
     *           {@link #authorizationModel} or {@link #authorizationModelLocation}.
     */
    Optional<String> authorizationTuples();

    /**
     * Location of JSON formatted authorization tuples file to upload during DevServices initialization.
     * <p>
     * The location can be prefixed with {@code classpath:} or {@code filesystem:} to specify where the file
     * will be read from; if not prefixed, it will be read from the classpath.
     * <p>
     *
     * @implNote Initialization of tuples will only happen if an authorization model is also defined via
     *           {@link #authorizationModel} or {@link #authorizationModelLocation}.
     */
    Optional<String> authorizationTuplesLocation();
}
