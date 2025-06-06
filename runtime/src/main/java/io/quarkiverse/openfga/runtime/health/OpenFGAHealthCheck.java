package io.quarkiverse.openfga.runtime.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import io.quarkiverse.openfga.client.api.API;
import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class OpenFGAHealthCheck implements AsyncHealthCheck {

    private final API api;

    @Inject
    public OpenFGAHealthCheck(API api) {
        this.api = api;
    }

    @Override
    public Uni<HealthCheckResponse> call() {

        final HealthCheckResponseBuilder builder = HealthCheckResponse.named("OpenFGA client connection health check");

        return api.health()
                .map(response -> {

                    if (response.status().equalsIgnoreCase("SERVING")) {
                        return builder.up().build();
                    }

                    return builder.down().withData("reported-status", response.status()).build();
                })
                .onFailure().recoverWithItem(x -> builder.withData("failure", x.getMessage()).build());
    }
}
