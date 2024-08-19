package io.quarkiverse.openfga.client;

import java.util.List;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.dto.ReadAssertionsResponse;
import io.quarkiverse.openfga.client.model.dto.WriteAssertionsRequest;
import io.smallrye.mutiny.Uni;

public class AssertionsClient {

    private final API api;
    private final Uni<String> storeId;
    private final String authorizationModelId;

    public AssertionsClient(API api, Uni<String> storeId, String authorizationModelId) {
        this.api = api;
        this.storeId = storeId;
        this.authorizationModelId = authorizationModelId;
    }

    public Uni<List<Assertion>> list() {
        return storeId.flatMap(storeId -> api.readAssertions(storeId, authorizationModelId))
                .map(ReadAssertionsResponse::getAssertions);
    }

    public Uni<Void> update(List<Assertion> assertions) {
        var request = WriteAssertionsRequest.builder()
                .authorizationModelId(authorizationModelId)
                .assertions(assertions)
                .build();
        return storeId.flatMap(storeId -> api.writeAssertions(storeId, request));
    }

}
