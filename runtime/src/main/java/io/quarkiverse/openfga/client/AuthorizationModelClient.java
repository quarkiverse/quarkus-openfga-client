package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.dto.*;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.client.utils.Pagination;
import io.smallrye.mutiny.Uni;

public class AuthorizationModelClient {

    private final API api;
    private final Uni<ClientConfig> config;

    public AuthorizationModelClient(API api, Uni<ClientConfig> config) {
        this.api = api;
        this.config = config;
    }

    public Uni<AuthorizationModel> get() {
        return config.flatMap(config -> api.readAuthorizationModel(config.getStoreId(), config.getAuthorizationModelId()))
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
        return config
                .flatMap(config -> {
                    var request = CheckRequest.builder()
                            .authorizationModelId(config.getAuthorizationModelId())
                            .tupleKey(tupleKey)
                            .contextualTuples(ContextualTupleKeys.of(conditionalTuples))
                            .context(context)
                            .consistency(consistency)
                            .build();
                    return api.check(config.getStoreId(), request);
                })
                .map(CheckResponse::getAllowed);
    }

    public Uni<UsersetTree> expand(ExpandTupleKey tupleKey) {
        return expand(tupleKey, null);
    }

    public Uni<UsersetTree> expand(ExpandTupleKey tupleKey, @Nullable ConsistencyPreference consistency) {
        return config.flatMap(config -> {
            var request = ExpandRequest.builder()
                    .tupleKey(tupleKey)
                    .authorizationModelId(config.getAuthorizationModelId())
                    .consistency(consistency)
                    .build();
            return api.expand(config.getStoreId(), request);
        })
                .map(ExpandResponse::getTree);
    }

    public record ListOptions(@Nullable List<ConditionalTupleKey> contextualTuples, @Nullable Object context,
            @Nullable ConsistencyPreference consistency) {

        public static final ListOptions NONE = new ListOptions(null, null, null);

        public static ListOptions of(List<ConditionalTupleKey> contextualTuples, Object context,
                ConsistencyPreference consistency) {
            return new ListOptions(contextualTuples, context, consistency);
        }

        public ListOptions() {
            this(null, null, null);
        }

        public ListOptions contextualTuples(List<ConditionalTupleKey> contextualTuples) {
            return new ListOptions(contextualTuples, context, consistency);
        }

        public ListOptions context(Object context) {
            return new ListOptions(contextualTuples, context, consistency);
        }

        public ListOptions consistency(ConsistencyPreference consistency) {
            return new ListOptions(contextualTuples, context, consistency);
        }
    }

    public Uni<List<String>> listObjects(String type, String relation, String user) {
        return listObjects(type, relation, user, ListOptions.NONE);
    }

    public Uni<List<String>> listObjects(String type, String relation, String user, ListOptions options) {
        return config.flatMap(config -> {
            var request = ListObjectsRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .type(type)
                    .relation(relation)
                    .user(user)
                    .contextualTuples(ContextualTupleKeys.of(options.contextualTuples))
                    .context(options.context)
                    .consistency(options.consistency)
                    .build();
            return api.listObjects(config.getStoreId(), request);
        }).map(ListObjectsResponse::getObjects);
    }

    public Uni<List<User>> listUsers(AnyObject object, String relation, List<UserTypeFilter> userFilters) {
        return listUsers(object, relation, userFilters, ListOptions.NONE);
    }

    public Uni<List<User>> listUsers(AnyObject object, String relation, List<UserTypeFilter> userFilters,
            ListOptions options) {
        return config.flatMap(config -> {
            var request = ListUsersRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .object(object)
                    .relation(relation)
                    .userFilters(userFilters)
                    .contextualTuples(ContextualTupleKeys.of(options.contextualTuples))
                    .context(options.context)
                    .consistency(options.consistency)
                    .build();
            return api.listUsers(config.getStoreId(), request);
        }).map(ListUsersResponse::getUsers);
    }

    @Deprecated(since = "2.4.0", forRemoval = true)
    public Uni<PaginatedList<Tuple>> queryTuples(PartialTupleKey tupleKey, @Nullable Integer pageSize,
            @Nullable String continuationToken) {
        return queryTuples(tupleKey, Pagination.limitedTo(pageSize).andContinuingFrom(continuationToken));
    }

    public Uni<PaginatedList<Tuple>> queryTuples(PartialTupleKey tupleKey) {
        return queryTuples(tupleKey, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<Tuple>> queryTuples(PartialTupleKey tupleKey, Pagination options) {
        return config
                .flatMap(config -> {
                    var request = ReadRequest.builder()
                            .tupleKey(tupleKey)
                            .authorizationModelId(config.getAuthorizationModelId())
                            .pageSize(options.pageSize())
                            .continuationToken(options.continuationToken())
                            .build();
                    return api.read(config.getStoreId(), request);
                })
                .map(res -> new PaginatedList<>(res.getTuples(), res.getContinuationToken()));
    }

    public Uni<List<Tuple>> queryAllTuples(PartialTupleKey tupleKey) {
        return queryAllTuples(tupleKey, null);
    }

    public Uni<List<Tuple>> queryAllTuples(PartialTupleKey tupleKey, @Nullable Integer pageSize) {
        return collectAllPages(pageSize, (paginationOptions) -> queryTuples(tupleKey, paginationOptions));
    }

    @Deprecated(since = "2.4.0", forRemoval = true)
    public Uni<PaginatedList<Tuple>> readTuples(@Nullable Integer pageSize, @Nullable String continuationToken) {
        return readTuples(Pagination.limitedTo(pageSize).andContinuingFrom(continuationToken));
    }

    public Uni<PaginatedList<Tuple>> readTuples() {
        return readTuples(Pagination.DEFAULT);
    }

    public Uni<PaginatedList<Tuple>> readTuples(Pagination options) {
        return config.flatMap(config -> {
            var request = ReadRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .pageSize(options.pageSize())
                    .continuationToken(options.continuationToken())
                    .build();
            return api.read(config.getStoreId(), request);
        }).map(res -> new PaginatedList<>(res.getTuples(), res.getContinuationToken()));
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
        return config.flatMap(config -> {
            var request = WriteRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .writes(WriteRequest.Writes.of(writes))
                    .deletes(WriteRequest.Deletes.of(deletes))
                    .build();
            return api.write(config.getStoreId(), request);
        })
                .map(WriteResponse::getValues);
    }

    public AssertionsClient assertions() {
        return new AssertionsClient(api, config);
    }

}
