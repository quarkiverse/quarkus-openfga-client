package io.quarkiverse.openfga.it;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.eclipse.microprofile.config.ConfigProvider.getConfig;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(OktaFGATest.Profile.class)
public class OktaFGATest extends SharedTest {

    public static class Profile implements QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "okta";
        }
    }

    @BeforeEach
    public void check() {
        var clientId = getConfig().getOptionalValue("quarkus.openfga.credentials.oidc.client-id", String.class);
        var clientSecret = getConfig().getOptionalValue("quarkus.openfga.credentials.oidc.client-secret", String.class);
        var fail = """
                OIDC Client %s is not set, skipping test. \
                Must be provided via OKTA_FGA_CLIENT_SECRET environment variable \
                (e.g., using a '.env` file).
                """;
        assumeThat(clientId)
                .withFailMessage(fail.formatted("ID"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .isNotEqualTo("--not-set--");
        assumeThat(clientSecret)
                .withFailMessage(fail.formatted("Secret"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .isNotEqualTo("--not-set--");
    }

}
