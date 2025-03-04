package io.quarkiverse.openfga.runtime.config;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = OpenFGAConfig.PREFIX)
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface OpenFGAConfig {

    @ConfigGroup
    interface Credentials {

        enum Method {
            @ConfigDocEnumValue("none")
            NONE,
            @ConfigDocEnumValue("preshared")
            PRESHARED,
            @ConfigDocEnumValue("oidc")
            OIDC,
        }

        @ConfigGroup
        interface OIDC {

            String DEFAULT_TOKEN_ISSUER_PATH = "/oauth/token";
            Duration DEFAULT_TOKEN_EXPIRATION_THRESHOLD = Duration.ofSeconds(300);
            Duration DEFAULT_TOKEN_EXPIRATION_THRESHOLD_JITTER = Duration.ofSeconds(60);

            /**
             * OAuth client id.
             * <p>
             * The client id is used to identify the client to the authorization server and is
             * provided by the authorization service.
             */
            String clientId();

            /**
             * OAuth client secret.
             * <p>
             * The client secret is used to authenticate the client to the authorization server
             * and is provided by the authorization service.
             */
            String clientSecret();

            /**
             * OAuth audience URI.
             * <p>
             * The audience for the access token, typically the URL of the service that will
             * consume the token.
             * This is generally configured in the authorization service.
             */
            URI audience();

            /**
             * OAuth scopes.
             * <p>
             * The scopes to request for the access token.
             */
            Optional<String> scopes();

            /**
             * OAuth token issuer URL.
             * <p>
             * The URL of the authorization service that will issue the access token.
             */
            URI tokenIssuer();

            /**
             * OAuth token issuer path.
             * <p>
             * The path of the token issuer endpoint, relative to the {@link #tokenIssuer()} URL.
             * <p>
             * Default value is {@code /oauth/token}.
             */
            Optional<String> tokenIssuerPath();

            /**
             * Token expiration threshold.
             * <p>
             * The duration before the token expiration at which the token should be refreshed.
             * <p>
             * Default value is {@code 300s}.
             */
            Optional<Duration> tokenExpirationThreshold();

            /**
             * Token expiration threshold jitter.
             * <p>
             * The maximum jitter to add to the token expiration threshold.
             * <p>
             * Default value is {@code 60s}.
             */
            Optional<Duration> tokenExpirationThresholdJitter();
        }

        @ConfigGroup
        interface Preshared {
            /**
             * Pre-shared authentication key.
             * <p>
             * The dev services OpenFGA instance will be configured with
             * these pre-shared keys for authentication.
             */
            String key();
        }

        /**
         * Credentials method to use for authentication.
         * <p>
         * When set to {@code preshared},
         * the required {@code preshared} configuration keys must be provided.
         * <p>
         * When set to {@code oidc},
         * the required {@code oidc} configuration keys must be provided.
         */
        @ConfigDocDefault("none")
        Optional<Method> method();

        /**
         * Pre-shared Authentication Key Credentials Configuration
         */
        @ConfigDocSection
        Optional<Preshared> preshared();

        /**
         * OIDC Client Credentials Configuration
         */
        @ConfigDocSection
        Optional<OIDC> oidc();
    }

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
     * Credentials Configuration
     */
    @ConfigDocSection
    Credentials credentials();

    /**
     * Shared authentication key.
     * <p>
     * This property is deprecated and will be removed in a future release,
     * use {@link Credentials.Preshared#key()} instead.
     */
    @Deprecated(since = "3.1.0", forRemoval = true)
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
     * If none is provided, the default bean will target the default authorization model for the store.
     */
    Optional<String> authorizationModelId();

    /**
     * The name of the TLS configuration to use.
     * <p>
     * If not set and the default TLS configuration is configured ({@code quarkus.tls.*}) then that will be used.
     * If a name is configured, it uses the configuration from {@code quarkus.tls.<name>.*}
     * If a name is configured, but no TLS configuration is found with that name then an error will be thrown.
     */
    Optional<String> tlsConfigurationName();

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
