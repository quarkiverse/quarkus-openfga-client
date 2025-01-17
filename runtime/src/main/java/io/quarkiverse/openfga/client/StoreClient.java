package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.time.OffsetDateTime;
import java.util.List;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.Tuple;
import io.quarkiverse.openfga.client.model.TupleChange;
import io.quarkiverse.openfga.client.model.dto.GetStoreResponse;
import io.quarkiverse.openfga.client.model.dto.ReadChangesRequest;
import io.quarkiverse.openfga.client.model.dto.ReadRequest;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.client.utils.Pagination;
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

    public record ReadChangesFilter(@Nullable String type, @Nullable OffsetDateTime startTime) {

        public static final ReadChangesFilter ALL = new ReadChangesFilter(null, null);

        public static ReadChangesFilter only(@Nullable String type) {
            return new ReadChangesFilter(type, null);
        }

        public static ReadChangesFilter since(@Nullable OffsetDateTime startTime) {
            return new ReadChangesFilter(null, startTime);
        }

        public ReadChangesFilter andOnly(@Nullable String type) {
            return new ReadChangesFilter(type, this.startTime);
        }

        public ReadChangesFilter andSince(@Nullable OffsetDateTime startTime) {
            return new ReadChangesFilter(this.type, startTime);
        }
    }

    @Deprecated(since = "2.4.0", forRemoval = true)
    public Uni<List<TupleChange>> listChanges(@Nullable String type, @Nullable Integer pageSize,
            @Nullable String continuationToken) {
        return readChanges(
                ReadChangesFilter.only(type),
                Pagination.limitedTo(pageSize).andContinuingFrom(continuationToken))
                .map(PaginatedList::getItems);
    }

    public Uni<PaginatedList<TupleChange>> readChanges() {
        return readChanges(ReadChangesFilter.ALL, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<TupleChange>> readChanges(ReadChangesFilter filter) {
        return readChanges(filter, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<TupleChange>> readChanges(Pagination pagination) {
        return readChanges(ReadChangesFilter.ALL, pagination);
    }

    public Uni<PaginatedList<TupleChange>> readChanges(ReadChangesFilter filter, Pagination pagination) {
        var request = ReadChangesRequest.builder()
                .type(filter.type)
                .startTime(filter.startTime)
                .pageSize(pagination.pageSize())
                .continuationToken(pagination.continuationToken())
                .build();
        return storeId.flatMap(storeId -> api.readChanges(storeId, request))
                .map(res -> new PaginatedList<>(res.getChanges(), res.getContinuationToken()));
    }

    public Uni<List<TupleChange>> readAllChanges() {
        return readAllChanges(null);
    }

    public Uni<List<TupleChange>> readAllChanges(@Nullable Integer pageSize) {
        return collectAllPages(pageSize, this::readChanges);
    }

    @Deprecated(since = "2.4.0", forRemoval = true)
    public Uni<PaginatedList<Tuple>> readTuples(@Nullable Integer pageSize, @Nullable String continuationToken) {
        return readTuples(Pagination.limitedTo(pageSize).andContinuingFrom(continuationToken));
    }

    public Uni<PaginatedList<Tuple>> readTuples() {
        return readTuples(Pagination.DEFAULT);
    }

    public Uni<PaginatedList<Tuple>> readTuples(Pagination pagination) {
        var request = ReadRequest.builder()
                .pageSize(pagination.pageSize())
                .continuationToken(pagination.continuationToken())
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

}
