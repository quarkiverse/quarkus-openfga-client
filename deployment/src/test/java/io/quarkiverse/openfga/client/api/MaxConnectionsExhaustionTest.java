package io.quarkiverse.openfga.client.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.runtime.configuration.DurationConverter;
import io.quarkus.tls.TlsConfiguration;
import io.quarkus.tls.TlsConfigurationRegistry;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.http.HttpServer;
import io.vertx.mutiny.ext.web.client.WebClient;

/**
 * Reproduces the production failure mode of an undersized connection pool: when more requests are
 * in flight than the pool can serve, Vert.x queues the surplus (the wait queue is unbounded by
 * default), so the caller's blocking {@code await().atMost(...)} eventually trips with an
 * {@link io.smallrye.mutiny.TimeoutException} — it is not the pool that throws. Raising
 * {@code quarkus.openfga.max-connections} to cover the concurrency removes the timeout.
 */
public class MaxConnectionsExhaustionTest {

    static final int CONCURRENT_REQUESTS = 40; // > 20 simultaneous connections
    static final int EXHAUSTED_POOL = 5; // the Vert.x default
    static final int SUFFICIENT_POOL = CONCURRENT_REQUESTS;
    static final Duration RESPONSE_DELAY = Duration.ofSeconds(1);

    static final TlsConfigurationRegistry NO_TLS = new TlsConfigurationRegistry() {
        @Override
        public Optional<TlsConfiguration> get(String name) {
            return Optional.empty();
        }

        @Override
        public Optional<TlsConfiguration> getDefault() {
            return Optional.empty();
        }

        @Override
        public void register(String name, TlsConfiguration configuration) {
        }
    };

    Vertx vertx;
    HttpServer slowServer;
    int slowPort;

    @BeforeEach
    void setUp() {
        vertx = Vertx.vertx();
        // Every request holds its connection for RESPONSE_DELAY before responding, so a small pool
        // forces the surplus requests to queue and wait for a connection to free up.
        slowServer = vertx.createHttpServer()
                .requestHandler(req -> vertx.getDelegate().setTimer(RESPONSE_DELAY.toMillis(),
                        id -> req.response().endAndForget("ok")))
                .listen(0)
                .await().indefinitely();
        slowPort = slowServer.actualPort();
    }

    @AfterEach
    void tearDown() {
        if (slowServer != null) {
            slowServer.closeAndAwait();
        }
        if (vertx != null) {
            vertx.closeAndAwait();
        }
    }

    OpenFGAConfig config(int maxConnections) {
        var props = Map.of(
                "quarkus.openfga.url", "http://localhost:" + slowPort,
                "quarkus.openfga.store", "test-store",
                "quarkus.openfga.max-connections", String.valueOf(maxConnections));
        return new SmallRyeConfigBuilder()
                .withMapping(OpenFGAConfig.class)
                .withConverter(Duration.class, 100, new DurationConverter())
                .withSources(new PropertiesConfigSource(props, "test", 100))
                .build()
                .getConfigMapping(OpenFGAConfig.class);
    }

    Uni<List<Void>> fireConcurrentRequests(int maxConnections) {
        var options = VertxWebClientFactory.createOptions(config(maxConnections), false, NO_TLS);
        var client = WebClient.create(vertx, options);

        List<Uni<Void>> calls = new ArrayList<>();
        for (var i = 0; i < CONCURRENT_REQUESTS; i++) {
            calls.add(client.get("/check").send().replaceWithVoid());
        }
        return Uni.join().all(calls).andFailFast();
    }

    @Test
    @DisplayName("An exhausted pool makes concurrent requests time out with a Mutiny TimeoutException")
    public void poolExhaustionCausesMutinyTimeout() {

        var combined = fireConcurrentRequests(EXHAUSTED_POOL);

        // Serving 40 requests through a pool of 5 takes ~8s; a 2s await cannot complete in time.
        assertThatThrownBy(() -> combined.await().atMost(Duration.ofSeconds(2)))
                .isInstanceOf(TimeoutException.class);
    }

    @Test
    @DisplayName("Raising max-connections to cover the load avoids the timeout")
    public void sufficientPoolAvoidsTimeout() {

        var combined = fireConcurrentRequests(SUFFICIENT_POOL);

        // With a connection per request, all 40 resolve in ~1s, well within the 6s await.
        var results = combined.await().atMost(Duration.ofSeconds(6));

        assertThat(results).hasSize(CONCURRENT_REQUESTS);
    }
}
