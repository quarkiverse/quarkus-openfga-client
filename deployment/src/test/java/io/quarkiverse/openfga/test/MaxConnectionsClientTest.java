package io.quarkiverse.openfga.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.Uni;

public class MaxConnectionsClientTest {

    // Raise the pool well above the Vert.x default of 5 (see application-with-max-connections.properties).
    static final int CONFIGURED_MAX_CONNECTIONS = 20;

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("application-with-max-connections.properties", "application.properties"));

    @Inject
    OpenFGAConfig config;

    @Inject
    OpenFGAClient client;

    @Test
    @DisplayName("Binds a connection pool size larger than the Vert.x default of 5")
    public void configuresMoreThanFiveConnections() {
        assertThat(config.maxConnections())
                .isGreaterThan(5)
                .isEqualTo(CONFIGURED_MAX_CONNECTIONS);
    }

    @Test
    @DisplayName("Handles more concurrent requests than the default pool size with the raised limit")
    public void handlesConcurrentRequestsBeyondDefaultPool() {

        List<Uni<List<Store>>> calls = new ArrayList<>();
        for (var i = 0; i < CONFIGURED_MAX_CONNECTIONS; i++) {
            calls.add(client.listAllStores());
        }

        List<List<Store>> results = Uni.join().all(calls).andFailFast()
                .await().indefinitely();

        assertThat(results).hasSize(CONFIGURED_MAX_CONNECTIONS);
    }
}
