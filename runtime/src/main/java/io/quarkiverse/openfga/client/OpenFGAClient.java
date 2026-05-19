package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;
import static java.lang.String.format;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.ErrorCode;
import io.quarkiverse.openfga.client.model.FGAValidationException;
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

    /**
     * Default time-to-live for the {@link #storeIdResolver storeIdResolver} success cache.
     * Long enough that high-frequency operations don't repeatedly page through
     * {@code listStores}, short enough that an out-of-band store recreation (e.g. a
     * dev-environment reset, swapping a store id under the same name) is picked
     * up within a bounded window instead of requiring a service restart.
     */
    public static final Duration STORE_ID_CACHE_TTL = Duration.ofSeconds(60);

    private record CachedStoreId(String id, long expiresAtNanos) {
    }

    public static Uni<String> storeIdResolver(API api, String storeIdOrName, boolean alwaysResolveStoreId) {
        return storeIdResolver(api, storeIdOrName, alwaysResolveStoreId, STORE_ID_CACHE_TTL);
    }

    /**
     * Tunable variant of {@link #storeIdResolver(API, String, boolean)}: lets callers
     * choose the success-cache TTL. The default (60s, see {@link #STORE_ID_CACHE_TTL})
     * is a reasonable balance for most workloads; use a shorter TTL when stores
     * are recreated frequently out-of-band (e.g. dev/test environments), or a
     * longer one when {@code listStores} round-trip latency dominates.
     * <p>
     * Only successful resolutions are cached; "no store with name X" failures
     * always re-pass through {@code listStores} on the next subscription. The
     * previous {@code .memoize().indefinitely()} behaviour cached the failure
     * too, which wedged the service for its entire lifetime if the store was
     * provisioned after app start — a common race during fresh stack bring-up,
     * since the recorded {@code Uni} is subscribed during readiness probes
     * before the store-init job has finished. Mutiny's stdlib
     * {@code .memoize()} variants ({@code forFixedDuration}, {@code atLeast},
     * {@code until}, {@code indefinitely}) all cache successes and failures
     * symmetrically, so this success-only behaviour is built directly on a
     * tiny {@code AtomicReference} of {@code (id, expiry)} instead.
     */
    public static Uni<String> storeIdResolver(API api, String storeIdOrName, boolean alwaysResolveStoreId,
            Duration cacheTtl) {
        if (STORE_ID_PATTERN.matcher(storeIdOrName).matches() && !alwaysResolveStoreId) {
            return Uni.createFrom().item(storeIdOrName);
        }

        // Concurrent first-callers may race to populate, but OpenFGA store ids
        // are stable within a TTL window so racing writes are idempotent.
        var cachedStoreId = new AtomicReference<CachedStoreId>();
        var cacheTtlNanos = cacheTtl.toNanos();

        return Uni.createFrom().deferred(() -> {
            var cached = cachedStoreId.get();
            if (cached != null && System.nanoTime() - cached.expiresAtNanos() < 0) {
                return Uni.createFrom().item(cached.id());
            }
            return resolveStoreIdOnce(api, storeIdOrName)
                    .onItem().invoke(id -> cachedStoreId.set(
                            new CachedStoreId(id, System.nanoTime() + cacheTtlNanos)));
        });
    }

    private static Uni<String> resolveStoreIdOnce(API api, String storeIdOrName) {
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
                .toUni();
    }

    public static Uni<String> authorizationModelIdResolver(API api, String storeId) {
        return api.listAuthorizationModels(storeId, ListAuthorizationModelsRequest.builder().pageSize(1).build())
                .map(ListAuthorizationModelsResponse::authorizationModels)
                .flatMap(models -> {
                    if (models.isEmpty()) {
                        var notFound = new FGAValidationException(
                                ErrorCode.LATEST_AUTHORIZATION_MODEL_NOT_FOUND,
                                "No default authorization model found");
                        return Uni.createFrom().failure(notFound);
                    }
                    return Uni.createFrom().item(models.get(0).getId());
                });
    }
}
