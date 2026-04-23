package io.quarkiverse.openfga.deployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.utility.DockerImageName;

import io.quarkiverse.openfga.client.model.AuthorizationModelSchema;
import io.quarkiverse.openfga.client.model.RelTupleKeys;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;

class DevServicesOpenFGAProcessorUnitTest {

    @AfterEach
    void tearDown() {
        DevServicesOpenFGAProcessor.resetLifecycleStateForTest();
    }

    @Test
    void serviceIdentityChangesWhenInitFingerprintsChange() throws Exception {
        var config = baseConfig(Map.of("OPENFGA_LOG_LEVEL", "debug"), true);
        var modelSchema = AuthorizationModelSchema.parse("""
                {
                  "schema_version": "1.1",
                  "type_definitions": []
                }
                """);
        var tuples = RelTupleKeys.parseList("""
                [
                  {"user":"user:anne","relation":"viewer","object":"document:1"}
                ]
                """).getTupleKeys();
        var runtimeIdentity = emptyRuntimeClientIdentity();

        var initA = new DevServicesOpenFGAProcessor.InitializationSpec(
                "dev",
                Optional.of(new DevServicesOpenFGAProcessor.LoadedAuthorizationModel(modelSchema, "model-a")),
                Optional.of(new DevServicesOpenFGAProcessor.LoadedAuthorizationTuples(tuples, "tuples-a")));
        var initB = new DevServicesOpenFGAProcessor.InitializationSpec(
                "dev",
                Optional.of(new DevServicesOpenFGAProcessor.LoadedAuthorizationModel(modelSchema, "model-b")),
                Optional.of(new DevServicesOpenFGAProcessor.LoadedAuthorizationTuples(tuples, "tuples-a")));

        var identityA = DevServicesOpenFGAProcessor.buildServiceIdentity(
                config,
                DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE,
                initA,
                runtimeIdentity);
        var identityB = DevServicesOpenFGAProcessor.buildServiceIdentity(
                config,
                DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE,
                initB,
                runtimeIdentity);

        assertThat(identityA).isNotEqualTo(identityB);
    }

    @Test
    void serviceIdentityChangesWhenRuntimeClientCredentialsChange() {
        var config = baseConfig(Map.of(), true);
        var initializationSpec = new DevServicesOpenFGAProcessor.InitializationSpec(
                "dev",
                Optional.empty(),
                Optional.empty());

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
            case DevServicesOpenFGAProcessor.CREDS_PRESHARED_KEY_KEY -> Optional.of("test-key");
            default -> Optional.empty();
        });

        var oidcIdentity = DevServicesOpenFGAProcessor.buildServiceIdentity(
                config,
                DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE,
                initializationSpec,
                oidcClientIdentity);
        var presharedIdentity = DevServicesOpenFGAProcessor.buildServiceIdentity(
                config,
                DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE,
                initializationSpec,
                presharedClientIdentity);

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

    @Test
    void containerEnvAndReuseAreAppliedToContainer() {
        var config = baseConfig(Map.of("OPENFGA_LOG_LEVEL", "debug"), true);

        var container = new DevServicesOpenFGAProcessor.QuarkusOpenFGAContainer(
                DockerImageName.parse(DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE)
                        .asCompatibleSubstituteFor(DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE),
                config,
                "default-network",
                false,
                Thread.currentThread().getContextClassLoader());

        assertThat(container.getEnvMap()).containsEntry("OPENFGA_LOG_LEVEL", "debug");
        assertThat(container.isShouldBeReused()).isTrue();
        assertThat(Arrays.asList(container.getCommandParts()))
                .contains("--authn-method=preshared")
                .contains("--authn-preshared-keys=test-key");
    }

    @Test
    void closeCachedDevServiceStateClosesAndClearsCachedState() {
        boolean[] closed = new boolean[1];
        var serviceIdentity = baseServiceIdentity();
        var runningDevService = new DevServicesResultBuildItem.RunningDevService(
                "openfga",
                "OpenFGA DevServices Instance",
                "container-1",
                () -> closed[0] = true,
                Map.of());

        DevServicesOpenFGAProcessor.cacheDevServiceState(runningDevService, serviceIdentity);
        DevServicesOpenFGAProcessor.closeCachedDevServiceState();

        assertThat(closed[0]).isTrue();
        assertThat(DevServicesOpenFGAProcessor.cachedDevService()).isNull();
        assertThat(DevServicesOpenFGAProcessor.cachedServiceIdentity()).isNull();
    }

    @Test
    void closeCachedDevServiceStateClearsCachedStateWhenCloseFails() {
        boolean[] closeAttempted = new boolean[1];
        var serviceIdentity = baseServiceIdentity();
        var runningDevService = new DevServicesResultBuildItem.RunningDevService(
                "openfga",
                "OpenFGA DevServices Instance",
                "container-2",
                () -> {
                    closeAttempted[0] = true;
                    throw new IOException("boom");
                },
                Map.of());

        DevServicesOpenFGAProcessor.cacheDevServiceState(runningDevService, serviceIdentity);
        DevServicesOpenFGAProcessor.closeCachedDevServiceState();

        assertThat(closeAttempted[0]).isTrue();
        assertThat(DevServicesOpenFGAProcessor.cachedDevService()).isNull();
        assertThat(DevServicesOpenFGAProcessor.cachedServiceIdentity()).isNull();
    }

    @Test
    void startOwnedServiceClosesContainerWhenConfigurationInitializationFails() {
        var container = new TestContainer(baseConfig(Map.of(), true));
        var expectedFailure = new RuntimeException("configuration failed");

        assertThatThrownBy(() -> DevServicesOpenFGAProcessor.startOwnedService(
                container,
                Map.of("quarkus.openfga.credentials.method", "preshared"),
                ignored -> {
                    throw expectedFailure;
                }))
                .isSameAs(expectedFailure);

        assertThat(container.started).isTrue();
        assertThat(container.closed).isTrue();
    }

    @Test
    void sharedDriftReasonsIncludeModelAndTuplesDifferences() {
        var reasons = DevServicesOpenFGAProcessor.sharedInitializationDriftReasons(
                Optional.of("model-a"),
                Optional.of("tuples-a"),
                Optional.of("model-b"),
                true);

        assertThat(reasons)
                .hasSize(2)
                .anyMatch(r -> r.contains("authorization model fingerprint"))
                .anyMatch(r -> r.contains("authorization tuples"));

        assertThat(DevServicesOpenFGAProcessor.sharedInitializationDriftReasons(
                Optional.of("model-a"),
                Optional.of("tuples-a"),
                Optional.of("model-a"),
                false))
                .isEmpty();
    }

    private static DevServicesOpenFGAProcessor.RuntimeClientCredentialsIdentity emptyRuntimeClientIdentity() {
        return DevServicesOpenFGAProcessor.runtimeClientCredentialsIdentity(key -> Optional.empty());
    }

    private static DevServicesOpenFGAProcessor.ServiceIdentity baseServiceIdentity() {
        return DevServicesOpenFGAProcessor.buildServiceIdentity(
                baseConfig(Map.of(), true),
                DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE,
                new DevServicesOpenFGAProcessor.InitializationSpec("dev", Optional.empty(), Optional.empty()),
                emptyRuntimeClientIdentity());
    }

    private static DevServicesOpenFGAConfig baseConfig(Map<String, String> containerEnv, boolean reuse) {
        return new TestConfig(
                Optional.of(true),
                Optional.empty(),
                containerEnv,
                reuse,
                true,
                DevServicesOpenFGAConfig.DEFAULT_SERVICE_NAME,
                OptionalInt.empty(),
                OptionalInt.empty(),
                OptionalInt.empty(),
                "dev",
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                new TestAuthentication(
                        DevServicesOpenFGAConfig.Authentication.Method.PRESHARED,
                        Optional.of(new TestPreshared(List.of("test-key"))),
                        Optional.empty()),
                Optional.empty(),
                Duration.ofSeconds(5));
    }

    private record TestConfig(Optional<Boolean> enabled,
            Optional<String> imageName,
            Map<String, String> containerEnv,
            boolean reuse,
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

    @SuppressWarnings("unused")
    private record TestOidc(String issuer,
            String audience,
            Optional<List<String>> issuerAliases,
            Optional<List<String>> subjects,
            Optional<List<String>> clientIdClaims) implements DevServicesOpenFGAConfig.Authentication.OIDC {
    }

    @SuppressWarnings("unused")
    private record TestTls(String pemCertificatePath,
            String pemKeyPath) implements DevServicesOpenFGAConfig.Tls {
    }

    private static final class TestContainer extends DevServicesOpenFGAProcessor.QuarkusOpenFGAContainer {
        boolean started;
        boolean closed;

        private TestContainer(DevServicesOpenFGAConfig config) {
            super(
                    DockerImageName.parse(DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE)
                            .asCompatibleSubstituteFor(DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE),
                    config,
                    "default-network",
                    false,
                    Thread.currentThread().getContextClassLoader());
        }

        @Override
        public void start() {
            started = true;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public String getContainerId() {
            return "test-container";
        }
    }
}
