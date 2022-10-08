package io.quarkiverse.openfga.runtime.config;

import static io.quarkiverse.openfga.runtime.config.OpenFGAConfig.NAME;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = NAME, phase = ConfigPhase.RUN_TIME)
public class OpenFGAConfig {

    public static final String NAME = "openfga";
    public static final String DEFAULT_CONNECT_TIMEOUT = "5S";
    public static final String DEFAULT_READ_TIMEOUT = "5S";

    /**
     * OpenFGA server URL.
     * <p>
     * Example: http://openfga:8080
     */
    @ConfigItem
    public URL url;

    /**
     * Shared authentication key.
     * <p>
     * If none provided unauthenticated access will be attempted.
     */
    @ConfigItem
    public Optional<String> sharedKey;

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
    @ConfigItem
    public String store;

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
    @ConfigItem(defaultValue = "false")
    public boolean alwaysResolveStoreId;

    /**
     * Authorization model id for default {@link AuthorizationModelClient} bean.
     * <p>
     * If none is provided the default bean will target the default authorization model for the store.
     */
    @ConfigItem
    public Optional<String> authorizationModelId;

    /**
     * TLS configuration.
     */
    @ConfigItem
    @ConfigDocSection
    public OpenFGATLSConfig tls;

    /**
     * Timeout to establish a connection with OpenFGA.
     */
    @ConfigItem(defaultValue = DEFAULT_CONNECT_TIMEOUT)
    public Duration connectTimeout;

    /**
     * Request timeout on OpenFGA.
     */
    @ConfigItem(defaultValue = DEFAULT_READ_TIMEOUT)
    public Duration readTimeout;

    /**
     * List of remote hosts that are not proxied when the client is configured to use a proxy. This
     * list serves the same purpose as the JVM {@code nonProxyHosts} configuration.
     *
     * <p>
     * Entries can use the <i>*</i> wildcard character for pattern matching, e.g <i>*.example.com</i> matches
     * <i>www.example.com</i>.
     */
    @ConfigItem
    public Optional<List<String>> nonProxyHosts;
}
