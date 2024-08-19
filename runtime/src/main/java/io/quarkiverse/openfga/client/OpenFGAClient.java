package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;
import static java.lang.String.format;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.dto.CreateStoreRequest;
import io.quarkiverse.openfga.client.model.dto.CreateStoreResponse;
import io.quarkiverse.openfga.client.model.dto.ListStoresRequest;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class OpenFGAClient {

    private final API api;

    public OpenFGAClient(API api) {
        this.api = api;
    }

    public Uni<PaginatedList<Store>> listStores(@Nullable Integer pageSize, @Nullable String continuationToken) {
        var request = ListStoresRequest.builder()
                .pageSize(pageSize)
                .continuationToken(continuationToken)
                .build();
        return api.listStores(request)
                .map(res -> new PaginatedList<>(res.getStores(), res.getContinuationToken()));
    }

    public Uni<List<Store>> listAllStores() {
        return listAllStores(null);
    }

    public Uni<List<Store>> listAllStores(@Nullable Integer pageSize) {
        return collectAllPages(pageSize, this::listStores);
    }

    public Uni<Store> createStore(String name) {
        var request = CreateStoreRequest.builder()
                .name(name)
                .build();
        return api.createStore(request).map(CreateStoreResponse::toStore);
    }

    public StoreClient store(String storeId) {
        return new StoreClient(api, Uni.createFrom().item(storeId));
    }

    private static class StoreSearchResult {
        Optional<String> storeId;
        Optional<String> token;

        StoreSearchResult(Optional<String> storeId, Optional<String> token) {
            this.storeId = storeId;
            this.token = token;
        }

        boolean isNotFinished() {
            return storeId.isEmpty() && token.isPresent();
        }
    }

    private static final Pattern STORE_ID_PATTERN = Pattern.compile("^[ABCDEFGHJKMNPQRSTVWXYZ0-9]{26}$");

    public static Uni<String> storeIdResolver(API api, String storeIdOrName, boolean alwaysResolveStoreId) {
        if (STORE_ID_PATTERN.matcher(storeIdOrName).matches() && !alwaysResolveStoreId) {
            return Uni.createFrom().item(storeIdOrName);
        }

        return Multi.createBy()
                .repeating().uni(AtomicReference<String>::new, lastToken -> {
                    var request = ListStoresRequest.builder()
                            .continuationToken(lastToken.get())
                            .build();
                    return api.listStores(request)
                            .onItem().invoke(list -> lastToken.set(list.getContinuationToken()))
                            .map(response -> {

                                var storeId = response.getStores().stream()
                                        .filter(store -> store.getName().equals(storeIdOrName)
                                                || store.getId().equals(storeIdOrName))
                                        .map(Store::getId)
                                        .findFirst();

                                Optional<String> token;
                                if (response.getContinuationToken() != null && !response.getContinuationToken().isEmpty()) {
                                    token = Optional.of(response.getContinuationToken());
                                } else {
                                    token = Optional.empty();
                                }

                                return new StoreSearchResult(storeId, token);
                            });
                })
                .whilst(StoreSearchResult::isNotFinished)
                .select().last()
                .flatMap(result -> {
                    if (result.storeId.isEmpty() && result.token.isEmpty()) {
                        return Multi.createFrom()
                                .failure(new IllegalStateException(format("No store with name '%s'", storeIdOrName)));
                    }
                    if (result.storeId.isEmpty()) {
                        return Multi.createFrom().nothing();
                    }
                    return Multi.createFrom().item(result.storeId.get());
                })
                .toUni()
                .memoize().indefinitely();
    }

}
