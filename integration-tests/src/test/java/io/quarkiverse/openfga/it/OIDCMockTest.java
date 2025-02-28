package io.quarkiverse.openfga.it;

import org.junit.jupiter.api.BeforeAll;

import io.quarkiverse.openfga.client.AssertionsClient;
import io.quarkus.arc.Arc;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(OIDCMockTest.Profile.class)
public class OIDCMockTest extends SharedTest {

    public static class Profile implements QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "oidc";
        }
    }

    @BeforeAll
    public static void beforeAll() {
        try (var client = Arc.container().instance(AssertionsClient.class)) {
            initAssertions(client.get());
        }
    }

}
