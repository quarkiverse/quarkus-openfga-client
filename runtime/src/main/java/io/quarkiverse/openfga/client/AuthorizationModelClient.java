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

    public AuthorizationModelClient(API api, Uni<String> storeId, String authorizationModelId) {
        this.api = api;
        this.storeId = storeId;
        this.authorizationModelId = authorizationModelId;
    }

    public Uni<AuthorizationModel> get() {
        return storeId.flatMap(storeId -> api.readAuthorizationModel(storeId, authorizationModelId))
                .map(ReadAuthorizationModelResponse::getAuthorizationModel);
    }

    public Uni<Boolean> check(TupleKey tupleKey) {
        return check(tupleKey, null, null, null);
    }

    public Uni<Boolean> check(TupleKey tupleKey, @Nullable List<ConditionalTupleKey> conditionalTuples) {
        return check(tupleKey, conditionalTuples, null, null);
    }

    public Uni<Boolean> check(TupleKey tupleKey, @Nullable List<ConditionalTupleKey> conditionalTuples,
            @Nullable Object context) {
        return check(tupleKey, conditionalTuples, context, null);
    }

    public Uni<Boolean> check(TupleKey tupleKey, @Nullable List<ConditionalTupleKey> conditionalTuples,
            @Nullable Object context, @Nullable ConsistencyPreference consistency) {
        var request = CheckRequest.builder()
                .authorizationModelId(authorizationModelId)
                .tupleKey(tupleKey)
                .contextualTuples(ContextualTupleKeys.of(conditionalTuples))
                .context(context)
                .consistency(consistency)
                .build();
        return storeId
                .flatMap(
                        storeId -> api.check(storeId, request))
                .map(CheckResponse::getAllowed);
    }

    public Uni<UsersetTree> expand(ExpandTupleKey tupleKey) {
        return expand(tupleKey, null);
    }

    public Uni<UsersetTree> expand(ExpandTupleKey tupleKey, @Nullable ConsistencyPreference consistency) {
        var request = ExpandRequest.builder()
                .tupleKey(tupleKey)
                .authorizationModelId(authorizationModelId)
                .consistency(consistency)
                .build();
        return storeId.flatMap(storeId -> api.expand(storeId, request))
                .map(ExpandResponse::getTree);
    }

    public static final class Options {
        private List<ConditionalTupleKey> contextualTuples;
        private Object context;
        private ConsistencyPreference consistency;

        public Options() {
            this.contextualTuples = null;
            this.context = null;
            this.consistency = null;
        }

        public Options contextualTuples(List<ConditionalTupleKey> contextualTuples) {
            this.contextualTuples = contextualTuples;
            return this;
        }

        public Options context(Object context) {
            this.context = context;
            return this;
        }

        public Options consistency(ConsistencyPreference consistency) {
            this.consistency = consistency;
            return this;
        }
    }

    public Uni<List<String>> listObjects(String type, String relation, String user) {
        return listObjects(type, relation, user, null);
    }

    public Uni<List<String>> listObjects(String type, String relation, String user, @Nullable Options options) {
        options = options == null ? new Options() : options;
        var request = ListObjectsRequest.builder()
                .authorizationModelId(authorizationModelId)
                .type(type)
                .relation(relation)
                .user(user)
                .contextualTuples(ContextualTupleKeys.of(options.contextualTuples))
                .context(options.context)
                .consistency(options.consistency)
                .build();
        return storeId.flatMap(storeId -> api.listObjects(storeId, request))
                .map(ListObjectsResponse::getObjects);
    }

    public Uni<List<User>> listUsers(AnyObject object, String relation, List<UserTypeFilter> userFilters) {
        return listUsers(object, relation, userFilters, null);
    }

    public Uni<List<User>> listUsers(AnyObject object, String relation, List<UserTypeFilter> userFilters,
            @Nullable Options options) {
        options = options == null ? new Options() : options;
        var request = ListUsersRequest.builder()
                .authorizationModelId(authorizationModelId)
                .object(object)
                .relation(relation)
                .userFilters(userFilters)
                .contextualTuples(ContextualTupleKeys.of(options.contextualTuples))
                .context(options.context)
                .consistency(options.consistency)
                .build();
        return storeId.flatMap(storeId -> api.listUsers(storeId, request))
                .map(ListUsersResponse::getUsers);
    }

    public Uni<PaginatedList<Tuple>> queryTuples(PartialTupleKey tupleKey, @Nullable Integer pageSize,
            @Nullable String continuationToken) {
        var request = ReadRequest.builder()
                .tupleKey(tupleKey)
                .authorizationModelId(authorizationModelId)
                .pageSize(pageSize)
                .continuationToken(continuationToken)
                .build();
        return storeId
                .flatMap(storeId -> api.read(storeId, request))
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

    public Uni<PaginatedList<Tuple>> readTuples(@Nullable Integer pageSize, @Nullable String continuationToken) {
        var request = ReadRequest.builder()
                .authorizationModelId(authorizationModelId)
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

    public Uni<Void> write(ConditionalTupleKey tupleKey) {
        return write(List.of(tupleKey), null)
                .replaceWithVoid();
    }

    public Uni<Map<String, Object>> write(@Nullable List<ConditionalTupleKey> writes, @Nullable List<TupleKey> deletes) {
        var request = WriteRequest.builder()
                .authorizationModelId(authorizationModelId)
                .writes(WriteRequest.Writes.of(writes))
                .deletes(WriteRequest.Deletes.of(deletes))
                .build();
        return storeId.flatMap(storeId -> api.write(storeId, request))
                .map(WriteResponse::getValues);
    }

}
