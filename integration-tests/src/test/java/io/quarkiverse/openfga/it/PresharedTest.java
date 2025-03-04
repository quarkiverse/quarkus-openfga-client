package io.quarkiverse.openfga.it;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(PresharedTest.Profile.class)
public class PresharedTest extends SharedTest {

    public static class Profile implements QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "preshared";
        }
    }

}
