package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.List;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.Tuple;
import io.quarkiverse.openfga.client.model.TupleChange;
import io.quarkiverse.openfga.client.model.dto.GetStoreResponse;
import io.quarkiverse.openfga.client.model.dto.ListChangesRequest;
import io.quarkiverse.openfga.client.model.dto.ListChangesResponse;
import io.quarkiverse.openfga.client.model.dto.ReadRequest;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.smallrye.mutiny.Uni;

public class StoreClient {

    private final API api;
    private final Uni<String> storeId;

    public StoreClient(API api, Uni<String> storeId) {
        this.api = api;
        this.storeId = storeId;
    }

    public Uni<Store> get() {
        return storeId.flatMap(api::getStore)
                .map(GetStoreResponse::asStore);
    }

    public Uni<Void> delete() {
        return storeId.flatMap(api::deleteStore);
    }

    public Uni<List<TupleChange>> listChanges(@Nullable String type, @Nullable Integer pageSize,
            @Nullable String continuationToken) {
        var request = ListChangesRequest.builder()
                .type(type)
                .pageSize(pageSize)
                .continuationToken(continuationToken)
                .build();
        return storeId.flatMap(storeId -> api.listChanges(storeId, request))
                .map(ListChangesResponse::getChanges);
    }

    public Uni<PaginatedList<Tuple>> readTuples(@Nullable Integer pageSize, @Nullable String continuationToken) {
        var request = ReadRequest.builder()
                .pageSize(pageSize)
                .continuationToken(continuationToken)
                .build();
        return storeId.flatMap(storeId -> api.read(storeId, request))
                .map(res -> new PaginatedList<>(res.getTuples(), res.getContinuationToken()));
    }

    public Uni<List<Tuple>> readAllTuples() {
        return readAllTuples(null);
    }

    public Uni<List<Tuple>> readAllTuples(@Nullable Integer pageSize) {
        return collectAllPages(pageSize, this::readTuples);
    }

    public AuthorizationModelsClient authorizationModels() {
        return new AuthorizationModelsClient(api, storeId);
    }

    public AssertionsClient assertions(String authorizationModelId) {
        return new AssertionsClient(api, storeId.map(storeId -> new ClientConfig(storeId, authorizationModelId)));
    }

}
