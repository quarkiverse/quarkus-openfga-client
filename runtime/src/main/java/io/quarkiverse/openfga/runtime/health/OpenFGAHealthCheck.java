package io.quarkiverse.openfga.runtime.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import io.quarkiverse.openfga.client.api.API;

public class OpenFGAHealthCheck implements HealthCheck {

    private final API api;
    private final String storeId;

    public OpenFGAHealthCheck(API api, String storeId) {
        this.api = api;
        this.storeId = storeId;
    }

    @Override
    public HealthCheckResponse call() {

        final HealthCheckResponseBuilder builder = HealthCheckResponse.named("OpenFGA client connection health check");

        try {
            api.getStore(storeId).await().indefinitely();

            builder.up();

        } catch (Exception e) {
            builder.down().withData("reason", e.getMessage()).build();
        }

        return builder.build();
    }
}
