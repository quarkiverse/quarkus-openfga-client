package io.quarkiverse.openfga.test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class OpenFGAClientTlsTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("application-with-tls.properties", "application.properties"));

    @Inject
    OpenFGAClient client;

    @Test
    @DisplayName("Can List Stores with TLS")
    public void canListStores() {
        assertThatNoException().isThrownBy(() -> client.listAllStores()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem());
    }

}
