package io.quarkiverse.openfga.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.junit.jupiter.api.Test;

class DevServicesOpenFGAProcessorUnitTest {

    @Test
    void serviceIdentityChangesWhenRuntimeClientCredentialsChange() {
        var devServicesConfig = new TestConfig(
                Optional.of(true),
                Optional.empty(),
                true,
                DevServicesOpenFGAConfig.DEFAULT_SERVICE_NAME,
                OptionalInt.empty(),
                OptionalInt.empty(),
                OptionalInt.empty(),
                "dev",
                Optional.empty(),
                Optional.of("classpath:auth-model.json"),
                Optional.empty(),
                Optional.of("classpath:auth-tuples.json"),
                new TestAuthentication(
                        DevServicesOpenFGAConfig.Authentication.Method.PRESHARED,
                        Optional.of(new TestPreshared(List.of("test-preshared-key"))),
                        Optional.empty()),
                Optional.empty(),
                Duration.ofSeconds(5));

        var oidcClientIdentity = DevServicesOpenFGAProcessor.runtimeClientCredentialsIdentity(key -> switch (key) {
            case DevServicesOpenFGAProcessor.CREDS_METHOD_KEY -> Optional.of("oidc");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_CLIENT_ID_KEY -> Optional.of("test-client-id");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_CLIENT_SECRET_KEY -> Optional.of("test-client-secret");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_AUDIENCE_KEY -> Optional.of("http://localhost:8080/openfga");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_TOKEN_ISSUER_KEY -> Optional.of("http://localhost:9999");
            default -> Optional.empty();
        });
        var presharedClientIdentity = DevServicesOpenFGAProcessor.runtimeClientCredentialsIdentity(key -> switch (key) {
            case DevServicesOpenFGAProcessor.CREDS_METHOD_KEY -> Optional.of("preshared");
            case DevServicesOpenFGAProcessor.CREDS_PRESHARED_KEY_KEY -> Optional.of("test-preshared-key");
            default -> Optional.empty();
        });

        var oidcIdentity = DevServicesOpenFGAProcessor.buildServiceIdentity(devServicesConfig, oidcClientIdentity);
        var presharedIdentity = DevServicesOpenFGAProcessor.buildServiceIdentity(devServicesConfig, presharedClientIdentity);

        assertThat(oidcIdentity).isNotEqualTo(presharedIdentity);
    }

    @Test
    void runtimeClientIdentityChangesWhenOidcIssuerChanges() {
        var first = DevServicesOpenFGAProcessor.runtimeClientCredentialsIdentity(key -> switch (key) {
            case DevServicesOpenFGAProcessor.CREDS_METHOD_KEY -> Optional.of("oidc");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_CLIENT_ID_KEY -> Optional.of("test-client-id");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_CLIENT_SECRET_KEY -> Optional.of("test-client-secret");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_AUDIENCE_KEY -> Optional.of("http://localhost:8080/openfga");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_TOKEN_ISSUER_KEY -> Optional.of("http://localhost:55623");
            default -> Optional.empty();
        });
        var second = DevServicesOpenFGAProcessor.runtimeClientCredentialsIdentity(key -> switch (key) {
            case DevServicesOpenFGAProcessor.CREDS_METHOD_KEY -> Optional.of("oidc");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_CLIENT_ID_KEY -> Optional.of("test-client-id");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_CLIENT_SECRET_KEY -> Optional.of("test-client-secret");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_AUDIENCE_KEY -> Optional.of("http://localhost:8080/openfga");
            case DevServicesOpenFGAProcessor.CREDS_OIDC_TOKEN_ISSUER_KEY -> Optional.of("http://localhost:55624");
            default -> Optional.empty();
        });

        assertThat(first).isNotEqualTo(second);
    }

    private record TestConfig(Optional<Boolean> enabled,
            Optional<String> imageName,
            boolean shared,
            String serviceName,
            OptionalInt httpPort,
            OptionalInt grpcPort,
            OptionalInt playgroundPort,
            String storeName,
            Optional<String> authorizationModel,
            Optional<String> authorizationModelLocation,
            Optional<String> authorizationTuples,
            Optional<String> authorizationTuplesLocation,
            Authentication authentication,
            Optional<Tls> tls,
            Duration startupTimeout) implements DevServicesOpenFGAConfig {
    }

    private record TestAuthentication(Method method,
            Optional<Preshared> preshared,
            Optional<OIDC> oidc) implements DevServicesOpenFGAConfig.Authentication {
    }

    private record TestPreshared(List<String> keys) implements DevServicesOpenFGAConfig.Authentication.Preshared {
    }
}
