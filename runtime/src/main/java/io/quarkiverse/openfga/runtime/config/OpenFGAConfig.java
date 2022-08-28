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
     */
    @ConfigItem
    public Optional<String> sharedKey;

    /**
     * Store id for default {@link StoreClient} bean.
     */
    @ConfigItem
    public String storeId;

    /**
     * Authorization model id for default {@link AuthorizationModelClient} bean.
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
     * Timeout to establish a connection with Vault.
     */
    @ConfigItem(defaultValue = DEFAULT_CONNECT_TIMEOUT)
    public Duration connectTimeout;

    /**
     * Request timeout on Vault.
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
