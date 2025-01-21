package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.RelObjectType;
import io.quarkiverse.openfga.client.model.RelTuple;
import io.quarkiverse.openfga.client.model.RelTupleChange;
import io.quarkiverse.openfga.client.model.Store;
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

    public record ReadChangesFilter(Optional<String> type, Optional<OffsetDateTime> startTime) {

        public static final ReadChangesFilter ALL = new ReadChangesFilter(Optional.empty(), Optional.empty());

        public static ReadChangesFilter only(@Nullable String type) {
            return new ReadChangesFilter(Optional.ofNullable(type), Optional.empty());
        }

        public static ReadChangesFilter only(@Nullable RelObjectType type) {
            return new ReadChangesFilter(Optional.ofNullable(type).map(RelObjectType::getType), Optional.empty());
        }

        public static ReadChangesFilter since(@Nullable OffsetDateTime startTime) {
            return new ReadChangesFilter(Optional.empty(), Optional.ofNullable(startTime));
        }

        public ReadChangesFilter andOnly(@Nullable String type) {
            return new ReadChangesFilter(Optional.ofNullable(type), this.startTime);
        }

        public ReadChangesFilter andSince(@Nullable OffsetDateTime startTime) {
            return new ReadChangesFilter(this.type, Optional.ofNullable(startTime));
        }
    }

    public Uni<PaginatedList<RelTupleChange>> readChanges() {
        return readChanges(ReadChangesFilter.ALL, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<RelTupleChange>> readChanges(ReadChangesFilter filter) {
        return readChanges(filter, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<RelTupleChange>> readChanges(Pagination pagination) {
        return readChanges(ReadChangesFilter.ALL, pagination);
    }

    public Uni<PaginatedList<RelTupleChange>> readChanges(ReadChangesFilter filter, Pagination pagination) {
        var request = ReadChangesRequest.builder()
                .type(filter.type.orElse(null))
                .startTime(filter.startTime.orElse(null))
                .pageSize(pagination.pageSize())
                .continuationToken(pagination.continuationToken().orElse(null))
                .build();
        return storeId.flatMap(storeId -> api.readChanges(storeId, request))
                .map(res -> new PaginatedList<>(res.changes(), res.continuationToken()));
    }

    public Uni<List<RelTupleChange>> readAllChanges(ReadChangesFilter filter) {
        return readAllChanges(filter, null);
    }

    public Uni<List<RelTupleChange>> readAllChanges(ReadChangesFilter filter, @Nullable Integer pageSize) {
        return collectAllPages(pageSize, pagination -> readChanges(filter, pagination));
    }

    public Uni<PaginatedList<RelTuple>> readTuples() {
        return readTuples(Pagination.DEFAULT);
    }

    public Uni<PaginatedList<RelTuple>> readTuples(Pagination pagination) {
        var request = ReadRequest.builder()
                .pageSize(pagination.pageSize())
                .continuationToken(pagination.continuationToken().orElse(null))
                .build();
        return storeId.flatMap(storeId -> api.read(storeId, request))
                .map(res -> new PaginatedList<>(res.tuples(), res.continuationToken()));
    }

    public Uni<List<RelTuple>> readAllTuples() {
        return readAllTuples(null);
    }

    public Uni<List<RelTuple>> readAllTuples(@Nullable Integer pageSize) {
        return collectAllPages(pageSize, this::readTuples);
    }

    public AuthorizationModelsClient authorizationModels() {
        return new AuthorizationModelsClient(api, storeId);
    }

}
