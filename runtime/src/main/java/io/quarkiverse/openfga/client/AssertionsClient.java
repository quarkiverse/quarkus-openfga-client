package io.quarkiverse.openfga.client;

import java.util.List;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.dto.ReadAssertionsResponse;
import io.quarkiverse.openfga.client.model.dto.WriteAssertionsRequest;
import io.smallrye.mutiny.Uni;

public class AssertionsClient {

    private final API api;
    private final Uni<ClientConfig> config;

    public AssertionsClient(API api, Uni<ClientConfig> config) {
        this.api = api;
        this.config = config;
    }

    public Uni<List<Assertion>> list() {
        return config.flatMap(config -> api.readAssertions(config.getStoreId(), config.getAuthorizationModelId()))
                .map(ReadAssertionsResponse::getAssertions);
    }

    public Uni<Void> update(List<Assertion> assertions) {
        return config.flatMap(config -> {
            var request = WriteAssertionsRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .assertions(assertions)
                    .build();
            return api.writeAssertions(config.getStoreId(), request);
        });
    }

}
