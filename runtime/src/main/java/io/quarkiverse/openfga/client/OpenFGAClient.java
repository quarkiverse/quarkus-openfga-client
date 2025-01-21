package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;
import static java.lang.String.format;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.FGAInputValidationException;
import io.quarkiverse.openfga.client.model.InputErrorCode;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.dto.*;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.client.utils.Pagination;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class OpenFGAClient {

    private final API api;

    public OpenFGAClient(API api) {
        this.api = api;
    }

    public record ListStoresFilter(@Nullable String name) {

        public static final ListStoresFilter ALL = new ListStoresFilter(null);

        public static ListStoresFilter named(String name) {
            return new ListStoresFilter(name);
        }
    }

    public Uni<PaginatedList<Store>> listStores() {
        return listStores(ListStoresFilter.ALL, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<Store>> listStores(ListStoresFilter filter) {
        return listStores(filter, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<Store>> listStores(Pagination pagination) {
        return listStores(ListStoresFilter.ALL, pagination);
    }

    public Uni<PaginatedList<Store>> listStores(ListStoresFilter filter, Pagination pagination) {
        var request = ListStoresRequest.builder()
                .name(filter.name())
                .pageSize(pagination.pageSize())
                .continuationToken(pagination.continuationToken().orElse(null))
                .build();
        return api.listStores(request)
                .map(res -> new PaginatedList<>(res.stores(), res.continuationToken()));
    }

    public Uni<List<Store>> listAllStores() {
        return listAllStores(ListStoresFilter.ALL, null);
    }

    public Uni<List<Store>> listAllStores(ListStoresFilter filter) {
        return listAllStores(filter, null);
    }

    public Uni<List<Store>> listAllStores(ListStoresFilter filter, @Nullable Integer pageSize) {
        return collectAllPages(pageSize, pagination -> listStores(filter, pagination));
    }

    public Uni<Store> createStore(String name) {
        var request = CreateStoreRequest.builder()
                .name(name)
                .build();
        return api.createStore(request).map(CreateStoreResponse::asStore);
    }

    public StoreClient store(String storeId) {
        return new StoreClient(api, Uni.createFrom().item(storeId));
    }

    private record StoreSearchResult(Optional<String> storeId, Optional<String> token) {

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
                            .onItem().invoke(list -> lastToken.set(list.continuationToken()))
                            .map(response -> {

                                var storeId = response.stores().stream()
                                        .filter(store -> store.getName().equals(storeIdOrName)
                                                || store.getId().equals(storeIdOrName))
                                        .map(Store::getId)
                                        .findFirst();

                                Optional<String> token;
                                if (response.continuationToken() != null && !response.continuationToken().isEmpty()) {
                                    token = Optional.of(response.continuationToken());
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

    public static Uni<String> authorizationModelIdResolver(API api, String storeId) {
        return api.listAuthorizationModels(storeId, ListAuthorizationModelsRequest.builder().pageSize(1).build())
                .map(ListAuthorizationModelsResponse::authorizationModels)
                .flatMap(models -> {
                    if (models.isEmpty()) {
                        var notFound = new FGAInputValidationException(
                                InputErrorCode.LATEST_AUTHORIZATION_MODEL_NOT_FOUND,
                                "No default authorization model found");
                        return Uni.createFrom().failure(notFound);
                    }
                    return Uni.createFrom().item(models.get(0).getId());
                });
    }
}
