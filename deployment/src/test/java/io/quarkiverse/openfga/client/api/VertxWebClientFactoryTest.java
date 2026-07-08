package io.quarkiverse.openfga.client.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.runtime.configuration.DurationConverter;
import io.quarkus.tls.TlsConfiguration;
import io.quarkus.tls.TlsConfigurationRegistry;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfigBuilder;

public class VertxWebClientFactoryTest {

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

    static OpenFGAConfig configWith(Map<String, String> overrides) {
        var props = new HashMap<String, String>();
        props.put("quarkus.openfga.url", "http://localhost:8080");
        props.put("quarkus.openfga.store", "test-store");
        props.putAll(overrides);
        return new SmallRyeConfigBuilder()
                .withMapping(OpenFGAConfig.class)
                .withConverter(Duration.class, 100, new DurationConverter())
                .withSources(new PropertiesConfigSource(props, "test", 100))
                .build()
                .getConfigMapping(OpenFGAConfig.class);
    }

    @Test
    @DisplayName("Defaults the connection pool to 5 when max-connections is not set")
    public void defaultsToFiveConnections() {
        var options = VertxWebClientFactory.createOptions(configWith(Map.of()), false, NO_TLS);
        assertThat(options.getMaxPoolSize()).isEqualTo(5);
    }

    @Test
    @DisplayName("Applies the configured max-connections to the web client pool size")
    public void usesConfiguredMaxConnections() {
        var options = VertxWebClientFactory.createOptions(
                configWith(Map.of("quarkus.openfga.max-connections", "42")), false, NO_TLS);
        assertThat(options.getMaxPoolSize()).isEqualTo(42);
    }
}
