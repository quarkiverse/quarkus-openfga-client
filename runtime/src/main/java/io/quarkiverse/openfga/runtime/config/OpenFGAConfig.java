package io.quarkiverse.openfga.runtime.config;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = OpenFGAConfig.PREFIX)
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface OpenFGAConfig {

    String PREFIX = "quarkus.openfga";
    String DEFAULT_CONNECT_TIMEOUT = "5S";
    String DEFAULT_READ_TIMEOUT = "5S";

    /**
     * OpenFGA server URL.
     * <p>
     * Example: <a href="http://openfga:8080">http://openfga:8080</a>
     */
    URL url();

    /**
     * Shared authentication key.
     * <p>
     * If none provided unauthenticated access will be attempted.
     */
    Optional<String> sharedKey();

    /**
     * Store id or name for default {@link StoreClient} bean.
     * <p>
     * If the provided property does not match the OpenFGA store id format
     * ({@code ^[ABCDEFGHJKMNPQRSTVWXYZ0-9]{26}$}) it will be treated as
     * a store name and a matching store id will be resolved at runtime.
     * <p>
     *
     * @see #alwaysResolveStoreId
     */
    String store();

    /**
     * Always Treat {@link #store} as the name of a store and resolve the
     * store id at runtime.
     * <p>
     * If true, the store id will always be resolved at runtime regardless
     * of the format of the {@link #store} property. Otherwise, the store
     * id will be resolved only when {@link #store} does not match the
     * OpenFGA store id format.
     * <p>
     *
     * @see #store
     */
    @WithDefault("false")
    boolean alwaysResolveStoreId();

    /**
     * Authorization model id for default {@link AuthorizationModelClient} bean.
     * <p>
     * If none is provided the default bean will target the default authorization model for the store.
     */
    Optional<String> authorizationModelId();

    /**
     * TLS configuration.
     */
    @ConfigDocSection
    OpenFGATLSConfig tls();

    /**
     * Timeout to establish a connection with OpenFGA.
     */
    @WithDefault(DEFAULT_CONNECT_TIMEOUT)
    Duration connectTimeout();

    /**
     * Request timeout on OpenFGA.
     */
    @WithDefault(DEFAULT_READ_TIMEOUT)
    Duration readTimeout();

    /**
     * List of remote hosts that are not proxied when the client is configured to use a proxy. This
     * list serves the same purpose as the JVM {@code nonProxyHosts} configuration.
     *
     * <p>
     * Entries can use the <i>*</i> wildcard character for pattern matching, e.g <i>*.example.com</i> matches
     * <i>www.example.com</i>.
     */
    Optional<List<String>> nonProxyHosts();
}
