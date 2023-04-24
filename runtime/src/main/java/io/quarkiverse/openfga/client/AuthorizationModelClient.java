package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.dto.*;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.smallrye.mutiny.Uni;

public class AuthorizationModelClient {

    private final API api;
    private final Uni<String> storeId;
    private final String authorizationModelId;

    public AuthorizationModelClient(API api, Uni<String> storeId, @Nullable String authorizationModelId) {
        this.api = api;
        this.storeId = storeId;
        this.authorizationModelId = authorizationModelId;
    }

    public Uni<AuthorizationModel> get() {
        return storeId.flatMap(storeId -> api.readAuthorizationModel(storeId, authorizationModelId))
                .map(ReadAuthorizationModelResponse::getAuthorizationModel);
    }

    public Uni<Boolean> check(TupleKey tupleKey, @Nullable ContextualTupleKeys contextualTupleKeys) {
        return storeId
                .flatMap(
                        storeId -> api.check(storeId, new CheckBody(tupleKey, contextualTupleKeys, authorizationModelId, null)))
                .map(CheckResponse::getAllowed);
    }

    public Uni<UsersetTree> expand(TupleKey tupleKey) {
        return storeId.flatMap(storeId -> api.expand(storeId, new ExpandBody(tupleKey, authorizationModelId)))
                .map(ExpandResponse::getTree);
    }

    public Uni<List<String>> listObjects(String type, @Nullable String relation, String user,
            @Nullable List<TupleKey> contextualTupleKeys) {
        return storeId.flatMap(storeId -> api.listObjects(storeId,
                new ListObjectsBody(authorizationModelId, type, relation, user,
                        contextualTupleKeys != null ? new ContextualTupleKeys(contextualTupleKeys) : null)))
                .map(ListObjectsResponse::getObjects);
    }

    public Uni<PaginatedList<Tuple>> queryTuples(PartialTupleKey tupleKey, @Nullable Integer pageSize,
            @Nullable String pagingToken) {
        return storeId
                .flatMap(storeId -> api.read(storeId, new ReadBody(tupleKey, authorizationModelId, pageSize, pagingToken)))
                .map(res -> new PaginatedList<>(res.getTuples(), res.getContinuationToken()));
    }

    public Uni<List<Tuple>> queryAllTuples(PartialTupleKey tupleKey) {
        return queryAllTuples(tupleKey, null);
    }

    public Uni<List<Tuple>> queryAllTuples(PartialTupleKey tupleKey, @Nullable Integer pageSize) {
        return collectAllPages(pageSize, (currentPageSize, currentToken) -> {
            return queryTuples(tupleKey, currentPageSize, currentToken);
        });
    }

    public Uni<PaginatedList<Tuple>> readTuples(@Nullable Integer pageSize, @Nullable String pagingToken) {
        return storeId.flatMap(storeId -> api.read(storeId, new ReadBody(null, authorizationModelId, pageSize, pagingToken)))
                .map(res -> new PaginatedList<>(res.getTuples(), res.getContinuationToken()));
    }

    public Uni<List<Tuple>> readAllTuples() {
        return readAllTuples(null);
    }

    public Uni<List<Tuple>> readAllTuples(@Nullable Integer pageSize) {
        return collectAllPages(pageSize, this::readTuples);
    }

    public Uni<Void> write(TupleKey tupleKey) {
        return write(List.of(tupleKey), null)
                .replaceWithVoid();
    }

    public Uni<Map<String, Object>> write(@Nullable List<TupleKey> writes, @Nullable List<TupleKey> deletes) {
        var writeKeys = TupleKeys.of(writes);
        var deleteKeys = TupleKeys.of(deletes);
        return storeId.flatMap(storeId -> api.write(storeId, new WriteBody(writeKeys, deleteKeys, authorizationModelId)))
                .map(WriteResponse::getValues);
    }

}
