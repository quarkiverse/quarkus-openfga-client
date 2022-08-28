package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.List;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.Tuple;
import io.quarkiverse.openfga.client.model.TupleChange;
import io.quarkiverse.openfga.client.model.dto.GetStoreResponse;
import io.quarkiverse.openfga.client.model.dto.ReadChangesResponse;
import io.quarkiverse.openfga.client.model.dto.ReadTuplesBody;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.smallrye.mutiny.Uni;

public class StoreClient {

    private final API api;
    private final String storeId;

    public StoreClient(API api, String storeId) {
        this.api = api;
        this.storeId = storeId;

    }

    public Uni<Store> get() {
        return api.getStore(storeId).map(GetStoreResponse::asStore);
    }

    public Uni<Void> delete() {
        return api.deleteStore(storeId);
    }

    public Uni<List<TupleChange>> changes(@Nullable String type, @Nullable Integer pageSize,
            @Nullable String continuationToken) {
        return api.readChanges(storeId, type, pageSize, continuationToken)
                .map(ReadChangesResponse::getChanges);
    }

    public Uni<PaginatedList<Tuple>> readTuples(@Nullable Integer pageSize, @Nullable String pagingToken) {
        return api.readTuples(storeId, new ReadTuplesBody(pageSize, pagingToken))
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
        return new AssertionsClient(api, storeId, authorizationModelId);
    }

}
