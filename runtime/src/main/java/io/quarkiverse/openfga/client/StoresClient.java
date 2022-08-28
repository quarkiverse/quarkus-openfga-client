package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.List;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.dto.CreateStoreRequest;
import io.quarkiverse.openfga.client.model.dto.CreateStoreResponse;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class StoresClient {

    private final API api;

    @Inject
    public StoresClient(API api) {
        this.api = api;
    }

    public Uni<PaginatedList<Store>> list(@Nullable Integer pageSize, @Nullable String continuationToken) {
        return api.listStores(pageSize, continuationToken)
                .map(res -> new PaginatedList<>(res.getStores(), res.getContinuationToken()));
    }

    public Uni<List<Store>> listAll() {
        return listAll(null);
    }

    public Uni<List<Store>> listAll(@Nullable Integer pageSize) {
        return collectAllPages(pageSize, this::list);
    }

    public Uni<Store> create(String name) {
        return api.createStore(new CreateStoreRequest(name)).map(CreateStoreResponse::asStore);
    }

    public StoreClient store(String storeId) {
        return new StoreClient(api, storeId);
    }

}
