package io.quarkiverse.openfga.it;

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

}
