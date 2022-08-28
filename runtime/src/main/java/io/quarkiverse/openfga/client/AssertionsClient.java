package io.quarkiverse.openfga.client;

import java.util.List;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.dto.ReadAssertionsResponse;
import io.quarkiverse.openfga.client.model.dto.WriteAssertionsRequest;
import io.smallrye.mutiny.Uni;

public class AssertionsClient {

    private final API api;
    private final String storeId;
    private final String authorizationModelId;

    public AssertionsClient(API api, String storeId, String authorizationModelId) {
        this.api = api;
        this.storeId = storeId;
        this.authorizationModelId = authorizationModelId;
    }

    public Uni<List<Assertion>> list() {
        return api.readAssertions(storeId, authorizationModelId)
                .map(ReadAssertionsResponse::getAssertions);
    }

    public Uni<Void> update(List<Assertion> assertions) {
        return api.writeAssertions(storeId, authorizationModelId, new WriteAssertionsRequest(assertions));
    }

}
