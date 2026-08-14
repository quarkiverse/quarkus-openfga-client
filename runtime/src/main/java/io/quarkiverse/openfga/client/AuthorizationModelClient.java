package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.dto.*;
import io.quarkiverse.openfga.client.model.utils.Preconditions;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.client.utils.Pagination;
import io.smallrye.mutiny.Multi;
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
                .map(ReadAuthorizationModelResponse::authorizationModel);
    }

    public record CheckOptions(Optional<Collection<? extends RelTupleKeyed>> contextualTuples,
            Optional<Map<String, Object>> context, Optional<ConsistencyPreference> consistency) {

        public static final CheckOptions DEFAULT = new CheckOptions();

        public CheckOptions() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static CheckOptions withContextualTuples(@Nullable Collection<? extends RelTupleKeyed> contextualTuples) {
            return new CheckOptions(Optional.ofNullable(contextualTuples), Optional.empty(), Optional.empty());
        }

        public static CheckOptions withContextualTuples(RelTupleKeyed... contextualTuples) {
            return withContextualTuples(List.of(contextualTuples));
        }

        public static CheckOptions withContext(@Nullable Map<String, Object> context) {
            return new CheckOptions(Optional.empty(), Optional.ofNullable(context), Optional.empty());
        }

        public static CheckOptions withConsistency(@Nullable ConsistencyPreference consistency) {
            return new CheckOptions(Optional.empty(), Optional.empty(), Optional.ofNullable(consistency));
        }

        public CheckOptions contextualTuples(@Nullable Collection<? extends RelTupleKeyed> contextualTuples) {
            return new CheckOptions(Optional.ofNullable(contextualTuples), context, consistency);
        }

        public CheckOptions contextualTuples(RelTupleKeyed... contextualTuples) {
            return contextualTuples(List.of(contextualTuples));
        }

        public CheckOptions context(@Nullable Map<String, Object> context) {
            return new CheckOptions(contextualTuples, Optional.ofNullable(context), consistency);
        }

        public CheckOptions consistency(@Nullable ConsistencyPreference consistency) {
            return new CheckOptions(contextualTuples, context, Optional.ofNullable(consistency));
        }
    }

    public Uni<Boolean> check(RelTupleKeyed relKey) {
        return check(relKey, CheckOptions.DEFAULT);
    }

    public Uni<Boolean> check(RelTupleKeyed relKey, CheckOptions options) {
        return checkResponse(relKey, options, null).map(CheckResponse::allowed);
    }

    /**
     * Checks a relationship and requests resolution details from OpenFGA.
     *
     * @param relKey relationship to check
     * @return detailed check response
     */
    public Uni<CheckResponse> checkDetailed(RelTupleKeyed relKey) {
        return checkDetailed(relKey, CheckOptions.DEFAULT);
    }

    /**
     * Checks a relationship and requests resolution details from OpenFGA.
     *
     * @param relKey relationship to check
     * @param options check options
     * @return detailed check response
     */
    public Uni<CheckResponse> checkDetailed(RelTupleKeyed relKey, CheckOptions options) {
        return checkResponse(relKey, options, true);
    }

    private Uni<CheckResponse> checkResponse(RelTupleKeyed relKey, CheckOptions options, @Nullable Boolean trace) {
        return config
                .flatMap(config -> {
                    var request = CheckRequest.builder()
                            .authorizationModelId(config.getAuthorizationModelId())
                            .tupleKey(relKey)
                            .contextualTuples(options.contextualTuples.map(RelTupleKeys::of).orElse(null))
                            .trace(trace)
                            .context(options.context.orElse(null))
                            .consistency(options.consistency.orElse(null))
                            .build();
                    return api.check(config.getStoreId(), request);
                });
    }

    public record BatchCheckOptions(Optional<ConsistencyPreference> consistency) {

        public static final BatchCheckOptions DEFAULT = new BatchCheckOptions();

        public BatchCheckOptions() {
            this(Optional.empty());
        }

        public static BatchCheckOptions withConsistency(@Nullable ConsistencyPreference consistency) {
            return new BatchCheckOptions(Optional.ofNullable(consistency));
        }

        public BatchCheckOptions consistency(@Nullable ConsistencyPreference consistency) {
            return new BatchCheckOptions(Optional.ofNullable(consistency));
        }
    }

    public Uni<Map<String, CheckResult>> batchCheck(Collection<Check> checks) {
        return batchCheck(checks, BatchCheckOptions.DEFAULT);
    }

    public Uni<Map<String, CheckResult>> batchCheck(Collection<Check> checks, BatchCheckOptions options) {
        return config.flatMap(config -> {
            var request = BatchCheckRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .checks(checks)
                    .consistency(options.consistency.orElse(null))
                    .build();
            return api.batchCheck(config.getStoreId(), request);
        }).map(BatchCheckResponse::result);
    }

    public record ExpandOptions(Optional<Collection<? extends RelTupleKeyed>> contextualTuples,
            Optional<Map<String, Object>> context, Optional<ConsistencyPreference> consistency) {

        public static final ExpandOptions DEFAULT = new ExpandOptions();

        public ExpandOptions() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static ExpandOptions withContextualTuples(@Nullable Collection<? extends RelTupleKeyed> contextualTuples) {
            return new ExpandOptions(Optional.ofNullable(contextualTuples), Optional.empty(), Optional.empty());
        }

        public static ExpandOptions withContextualTuples(RelTupleKeyed... contextualTuples) {
            return withContextualTuples(List.of(contextualTuples));
        }

        /**
         * Retains an expand context for source compatibility. OpenFGA does not accept it on the wire.
         *
         * @param context ignored context
         * @return expand options
         * @deprecated OpenFGA Expand does not accept evaluation context
         */
        @Deprecated(since = "3.15", forRemoval = true)
        public static ExpandOptions withContext(@Nullable Map<String, Object> context) {
            return new ExpandOptions(Optional.empty(), Optional.ofNullable(context), Optional.empty());
        }

        public static ExpandOptions withConsistency(@Nullable ConsistencyPreference consistency) {
            return new ExpandOptions(Optional.empty(), Optional.empty(), Optional.ofNullable(consistency));
        }

        public ExpandOptions contextualTuples(@Nullable Collection<? extends RelTupleKeyed> contextualTuples) {
            return new ExpandOptions(Optional.ofNullable(contextualTuples), context, consistency);
        }

        public ExpandOptions contextualTuples(RelTupleKeyed... contextualTuples) {
            return contextualTuples(List.of(contextualTuples));
        }

        /**
         * Retains an expand context for source compatibility. OpenFGA does not accept it on the wire.
         *
         * @param context ignored context
         * @return updated expand options
         * @deprecated OpenFGA Expand does not accept evaluation context
         */
        @Deprecated(since = "3.15", forRemoval = true)
        public ExpandOptions context(@Nullable Map<String, Object> context) {
            return new ExpandOptions(contextualTuples, Optional.ofNullable(context), consistency);
        }

        public ExpandOptions consistency(@Nullable ConsistencyPreference consistency) {
            return new ExpandOptions(contextualTuples, context, Optional.ofNullable(consistency));
        }
    }

    public Uni<Schema.UsersetTree> expand(RelPartialTupleKeyed tupleKey) {
        return expand(tupleKey, ExpandOptions.DEFAULT);
    }

    public Uni<Schema.UsersetTree> expand(RelPartialTupleKeyed tupleKey, ExpandOptions options) {
        return config.flatMap(config -> {
            var request = ExpandRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .tupleKey(tupleKey)
                    .contextualTuples(options.contextualTuples.map(RelTupleKeys::of).orElse(null))
                    .consistency(options.consistency.orElse(null))
                    .build();
            return api.expand(config.getStoreId(), request);
        })
                .map(ExpandResponse::tree);
    }

    public record ListOptions(Optional<Collection<? extends RelTupleKeyed>> contextualTuples,
            Optional<Map<String, Object>> context, Optional<ConsistencyPreference> consistency) {

        public static final ListOptions DEFAULT = new ListOptions();

        public static ListOptions of(@Nullable Collection<? extends RelTupleKeyed> contextualTuples,
                @Nullable Map<String, Object> context, @Nullable ConsistencyPreference consistency) {
            return new ListOptions(Optional.ofNullable(contextualTuples), Optional.ofNullable(context),
                    Optional.ofNullable(consistency));
        }

        public ListOptions() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static ListOptions withContextualTuples(@Nullable Collection<? extends RelTupleKeyed> contextualTuples) {
            return new ListOptions(Optional.ofNullable(contextualTuples), Optional.empty(), Optional.empty());
        }

        public static ListOptions withContextualTuples(RelTupleKeyed... contextualTuples) {
            return withContextualTuples(List.of(contextualTuples));
        }

        public static ListOptions withContext(@Nullable Map<String, Object> context) {
            return new ListOptions(Optional.empty(), Optional.ofNullable(context), Optional.empty());
        }

        public static ListOptions withConsistency(@Nullable ConsistencyPreference consistency) {
            return new ListOptions(Optional.empty(), Optional.empty(), Optional.ofNullable(consistency));
        }

        public ListOptions contextualTuples(@Nullable Collection<? extends RelTupleKeyed> contextualTuples) {
            return new ListOptions(Optional.ofNullable(contextualTuples), context, consistency);
        }

        public ListOptions contextualTuples(RelTupleKeyed... contextualTuples) {
            return contextualTuples(List.of(contextualTuples));
        }

        public ListOptions context(@Nullable Map<String, Object> context) {
            return new ListOptions(contextualTuples, Optional.ofNullable(context), consistency);
        }

        public ListOptions consistency(@Nullable ConsistencyPreference consistency) {
            return new ListOptions(contextualTuples, context, Optional.ofNullable(consistency));
        }
    }

    public record ListObjectsFilter(Optional<String> type, Optional<String> relation, Optional<RelEntity> user) {

        public ListObjectsFilter() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static ListObjectsFilter byObjectType(String type) {
            return new ListObjectsFilter().objectType(type);
        }

        public static ListObjectsFilter byObjectType(RelTyped type) {
            return new ListObjectsFilter().objectType(type);
        }

        public static ListObjectsFilter byRelation(String relation) {
            return new ListObjectsFilter().relation(relation);
        }

        public static ListObjectsFilter byUser(RelEntity user) {
            return new ListObjectsFilter().user(user);
        }

        public static ListObjectsFilter byUser(String user) {
            return new ListObjectsFilter().user(user);
        }

        public ListObjectsFilter objectType(String type) {
            return new ListObjectsFilter(Optional.of(type), relation, user);
        }

        public ListObjectsFilter objectType(RelTyped type) {
            return new ListObjectsFilter(Optional.of(type).map(RelTyped::getType), relation, user);
        }

        public ListObjectsFilter relation(String relation) {
            return new ListObjectsFilter(type, Optional.of(relation), user);
        }

        public ListObjectsFilter user(RelEntity user) {
            return new ListObjectsFilter(type, relation, Optional.of(user));
        }

        public ListObjectsFilter user(String user) {
            return new ListObjectsFilter(type, relation, Optional.of(user).map(RelUser::valueOf));
        }
    }

    public Uni<Collection<RelObject>> listObjects(ListObjectsFilter filter) {
        return listObjects(filter, ListOptions.DEFAULT);
    }

    public Uni<Collection<RelObject>> listObjects(ListObjectsFilter filter, ListOptions options) {
        return config.flatMap(config -> {
            var request = listObjectsRequest(config, filter, options);
            return api.listObjects(config.getStoreId(), request);
        }).map(ListObjectsResponse::objects);
    }

    /**
     * Streams objects matching the supplied filter.
     *
     * @param filter list objects filter
     * @return matching objects as OpenFGA produces them
     */
    public Multi<RelObject> streamObjects(ListObjectsFilter filter) {
        return streamObjects(filter, ListOptions.DEFAULT);
    }

    /**
     * Streams objects matching the supplied filter and options.
     *
     * @param filter list objects filter
     * @param options list objects options
     * @return matching objects as OpenFGA produces them
     */
    public Multi<RelObject> streamObjects(ListObjectsFilter filter, ListOptions options) {
        return config.toMulti()
                .onItem().transformToMultiAndConcatenate(config -> api.streamedListObjects(config.getStoreId(),
                        listObjectsRequest(config, filter, options)))
                .map(StreamedListObjectsResponse::object);
    }

    private static ListObjectsRequest listObjectsRequest(ClientConfig config, ListObjectsFilter filter,
            ListOptions options) {
        Preconditions.parameterNonNull(filter, "filter");
        var type = Preconditions.parameterNonNull(filter.type, "filter.type");
        var relation = Preconditions.parameterNonNull(filter.relation, "filter.relation");
        var user = Preconditions.parameterNonNull(filter.user, "filter.user");
        return ListObjectsRequest.builder()
                .authorizationModelId(config.getAuthorizationModelId())
                .type(type)
                .relation(relation)
                .user(user.asUser())
                .contextualTuples(options.contextualTuples.orElse(null))
                .context(options.context.orElse(null))
                .consistency(options.consistency.orElse(null))
                .build();
    }

    /**
     * Filter for listing users matching a specific object, relation, and user type(s).
     *
     * @param object The object to filter by.
     * @param relation The relation to filter by.
     * @param userFilters The user type filters to apply.
     */
    public record ListUsersFilter(Optional<RelEntity> object, Optional<String> relation,
            Optional<Collection<ListUsersRequest.UserTypeFilter>> userFilters) {

        private ListUsersFilter() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static ListUsersFilter byObject(RelEntity object) {
            return new ListUsersFilter().object(object);
        }

        public static ListUsersFilter byObject(String object) {
            return new ListUsersFilter().object(object);
        }

        public static ListUsersFilter byRelation(String relation) {
            return new ListUsersFilter().relation(relation);
        }

        public static ListUsersFilter byUserFilters(Collection<ListUsersRequest.UserTypeFilter> userFilters) {
            return new ListUsersFilter().userFilters(userFilters);
        }

        public static ListUsersFilter byUserFilters(ListUsersRequest.UserTypeFilter... userFilter) {
            return new ListUsersFilter().userFilters(userFilter);
        }

        public static ListUsersFilter byUserType(String type) {
            return new ListUsersFilter().userType(type);
        }

        public static ListUsersFilter byUserType(RelTyped type) {
            return new ListUsersFilter().userType(type);
        }

        public ListUsersFilter object(RelEntity object) {
            return new ListUsersFilter(Preconditions.parameterNonNullToOptional(object, "object"), relation, userFilters);
        }

        public ListUsersFilter object(String object) {
            return new ListUsersFilter(Preconditions.parameterNonNullToOptional(object, "object").map(RelObject::valueOf),
                    relation, userFilters);
        }

        public ListUsersFilter relation(String relation) {
            return new ListUsersFilter(object, Preconditions.parameterNonNullToOptional(relation, "relation"),
                    userFilters);
        }

        public ListUsersFilter userFilters(Collection<ListUsersRequest.UserTypeFilter> userFilters) {
            return new ListUsersFilter(object, relation,
                    Preconditions.parameterNonNullToOptional(userFilters, "userFilters"));
        }

        public ListUsersFilter userFilters(ListUsersRequest.UserTypeFilter... userFilters) {
            return new ListUsersFilter(object, relation,
                    Preconditions.parameterNonNullToOptional(userFilters, "userFilters").map(List::of));
        }

        public ListUsersFilter userType(String type) {
            return new ListUsersFilter(object, relation, Preconditions.parameterNonNullToOptional(type, "type")
                    .map(t -> List.of(ListUsersRequest.UserTypeFilter.builder().type(t).build())));
        }

        public ListUsersFilter userType(RelTyped type) {
            return new ListUsersFilter(object, relation, Optional.of(type)
                    .map(t -> List.of(ListUsersRequest.UserTypeFilter.builder().type(t.getType()).build())));
        }
    }

    public Uni<Collection<RelTyped>> listUsers(ListUsersFilter filter) {
        return listUsers(filter, ListOptions.DEFAULT);
    }

    public Uni<Collection<RelTyped>> listUsers(ListUsersFilter filter, ListOptions options) {
        Preconditions.parameterNonNull(filter, "filter");
        return config.flatMap(config -> {
            var request = ListUsersRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .object(filter.object.map(RelEntity::asObject).orElseThrow())
                    .relation(filter.relation.orElseThrow())
                    .userFilters(filter.userFilters.orElseThrow())
                    .contextualTuples(options.contextualTuples.orElse(null))
                    .context(options.context.orElse(null))
                    .consistency(options.consistency.orElse(null))
                    .build();
            return api.listUsers(config.getStoreId(), request);
        }).map(ListUsersResponse::asRel);
    }

    /**
     * Compatibility filter for the store-scoped Read API.
     *
     * @param typeOrObject optional object type or concrete object
     * @param relation optional relation
     * @param user optional user
     * @deprecated use {@link StoreClient.ReadTuplesFilter}
     */
    @Deprecated(since = "3.15", forRemoval = true)
    public record ReadFilter(Optional<RelTyped> typeOrObject, Optional<String> relation, Optional<RelEntity> user) {

        public static final ReadFilter ALL = new ReadFilter();

        public static ReadFilter byObjectType(@Nullable RelTyped type) {
            return ReadFilter.ALL.objectType(type);
        }

        public static ReadFilter byObjectType(@Nullable String type) {
            return ReadFilter.ALL.objectType(type);
        }

        public static ReadFilter byObject(@Nullable RelEntity object) {
            return ReadFilter.ALL.object(object);
        }

        public static ReadFilter byObject(@Nullable String object) {
            return ReadFilter.ALL.object(object);
        }

        public static ReadFilter byRelation(@Nullable String relation) {
            return ReadFilter.ALL.relation(relation);
        }

        public static ReadFilter byUser(@Nullable RelEntity user) {
            return ReadFilter.ALL.user(user);
        }

        public static ReadFilter byUser(@Nullable String user) {
            return ReadFilter.ALL.user(user);
        }

        public ReadFilter() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public ReadFilter objectType(@Nullable RelTyped type) {
            return new ReadFilter(Optional.ofNullable(type), relation, user);
        }

        public ReadFilter objectType(@Nullable String type) {
            return new ReadFilter(Optional.ofNullable(type).map(RelObjectType::of), relation, user);
        }

        public ReadFilter object(@Nullable RelEntity object) {
            return new ReadFilter(Optional.ofNullable(object), relation, user);
        }

        public ReadFilter object(@Nullable String object) {
            return new ReadFilter(Optional.ofNullable(object).map(RelObject::valueOf), relation, user);
        }

        public ReadFilter relation(@Nullable String relation) {
            return new ReadFilter(typeOrObject, Optional.ofNullable(relation), user);
        }

        public ReadFilter user(@Nullable RelEntity user) {
            return new ReadFilter(typeOrObject, relation, Optional.ofNullable(user));
        }

        public ReadFilter user(@Nullable String user) {
            return new ReadFilter(typeOrObject, relation, Optional.ofNullable(user).map(RelUser::valueOf));
        }
    }

    /**
     * Reads tuples from the store containing this model.
     *
     * @return a page of tuples
     * @deprecated use {@link StoreClient#readTuples()}
     */
    @Deprecated(since = "3.15", forRemoval = true)
    public Uni<PaginatedList<RelTuple>> read() {
        return read(ReadFilter.ALL, Pagination.DEFAULT);
    }

    /**
     * Reads filtered tuples from the store containing this model.
     *
     * @param filter tuple filter
     * @return a page of tuples
     * @deprecated use {@link StoreClient#readTuples(StoreClient.ReadTuplesFilter)}
     */
    @Deprecated(since = "3.15", forRemoval = true)
    public Uni<PaginatedList<RelTuple>> read(ReadFilter filter) {
        return read(filter, Pagination.DEFAULT);
    }

    /**
     * Reads tuples from the store containing this model.
     *
     * @param options pagination request
     * @return a page of tuples
     * @deprecated use {@link StoreClient#readTuples(Pagination)}
     */
    @Deprecated(since = "3.15", forRemoval = true)
    public Uni<PaginatedList<RelTuple>> read(Pagination options) {
        return read(ReadFilter.ALL, options);
    }

    /**
     * Reads filtered tuples from the store containing this model.
     *
     * @param filter tuple filter
     * @param options pagination request
     * @return a page of tuples
     * @deprecated use {@link StoreClient#readTuples(StoreClient.ReadTuplesFilter, Pagination)}
     */
    @Deprecated(since = "3.15", forRemoval = true)
    public Uni<PaginatedList<RelTuple>> read(ReadFilter filter, Pagination options) {
        return config.flatMap(config -> {
            var request = ReadRequest.builder()
                    .tupleKey(ReadRequest.TupleKeyFilter.builder()
                            .typeOrObject(filter.typeOrObject.orElse(null))
                            .relation(filter.relation.orElse(null))
                            .user(filter.user.map(RelEntity::asUser).orElse(null))
                            .build())
                    .pageSize(options.pageSize())
                    .continuationToken(options.continuationToken().orElse(null))
                    .build();
            return api.read(config.getStoreId(), request);
        }).map(res -> new PaginatedList<>(res.tuples(), res.continuationToken()));
    }

    /**
     * Reads all tuples from the store containing this model.
     *
     * @return all tuples
     * @deprecated use {@link StoreClient#readAllTuples()}
     */
    @Deprecated(since = "3.15", forRemoval = true)
    public Uni<List<RelTuple>> readAll() {
        return readAll(ReadFilter.ALL, Pagination.MAX.pageSize());
    }

    /**
     * Reads all filtered tuples from the store containing this model.
     *
     * @param filter tuple filter
     * @return all matching tuples
     * @deprecated use {@link StoreClient#readAllTuples(StoreClient.ReadTuplesFilter)}
     */
    @Deprecated(since = "3.15", forRemoval = true)
    public Uni<List<RelTuple>> readAll(ReadFilter filter) {
        return readAll(filter, Pagination.MAX.pageSize());
    }

    /**
     * Reads all filtered tuples from the store containing this model.
     *
     * @param filter tuple filter
     * @param pageSize page size
     * @return all matching tuples
     * @deprecated use {@link StoreClient#readAllTuples(StoreClient.ReadTuplesFilter, Integer,
     *             StoreClient.ReadTuplesOptions)}
     */
    @Deprecated(since = "3.15", forRemoval = true)
    public Uni<List<RelTuple>> readAll(ReadFilter filter, @Nullable Integer pageSize) {
        return collectAllPages(pageSize, (pagination) -> this.read(filter, pagination));
    }

    /**
     * Options controlling how OpenFGA handles tuple write conflicts.
     *
     * @param onDuplicate behavior when a written tuple already exists
     * @param onMissing behavior when a deleted tuple does not exist
     */
    public record WriteOptions(Optional<WriteConflictBehavior> onDuplicate,
            Optional<WriteConflictBehavior> onMissing) {

        /** Uses OpenFGA's default conflict behavior. */
        public static final WriteOptions DEFAULT = new WriteOptions();

        /** Creates options using OpenFGA's default conflict behavior. */
        public WriteOptions() {
            this(Optional.empty(), Optional.empty());
        }

        /**
         * Creates options for duplicate tuple writes.
         *
         * @param onDuplicate duplicate handling behavior
         * @return write options
         */
        public static WriteOptions withOnDuplicate(@Nullable WriteConflictBehavior onDuplicate) {
            return new WriteOptions(Optional.ofNullable(onDuplicate), Optional.empty());
        }

        /**
         * Creates options for missing tuple deletes.
         *
         * @param onMissing missing tuple handling behavior
         * @return write options
         */
        public static WriteOptions withOnMissing(@Nullable WriteConflictBehavior onMissing) {
            return new WriteOptions(Optional.empty(), Optional.ofNullable(onMissing));
        }

        /**
         * Sets duplicate tuple handling.
         *
         * @param onDuplicate duplicate handling behavior
         * @return updated write options
         */
        public WriteOptions onDuplicate(@Nullable WriteConflictBehavior onDuplicate) {
            return new WriteOptions(Optional.ofNullable(onDuplicate), onMissing);
        }

        /**
         * Sets missing tuple handling.
         *
         * @param onMissing missing tuple handling behavior
         * @return updated write options
         */
        public WriteOptions onMissing(@Nullable WriteConflictBehavior onMissing) {
            return new WriteOptions(onDuplicate, Optional.ofNullable(onMissing));
        }
    }

    public Uni<Map<String, Object>> write(RelTupleDefinition... tupleDefs) {
        return write(List.of(tupleDefs));
    }

    public Uni<Map<String, Object>> write(Collection<RelTupleDefinition> writes) {
        return write(writes, List.of());
    }

    /**
     * Writes tuples using the supplied conflict options.
     *
     * @param writes tuples to write
     * @param options write options
     * @return OpenFGA write response values
     */
    public Uni<Map<String, Object>> write(Collection<RelTupleDefinition> writes, WriteOptions options) {
        return write(writes, List.of(), options);
    }

    public Uni<Map<String, Object>> delete(RelTupleDefinition... tupleDefs) {
        return delete(List.of(tupleDefs));
    }

    public Uni<Map<String, Object>> delete(Collection<RelTupleDefinition> deletes) {
        return write(List.of(), deletes);
    }

    /**
     * Deletes tuples using the supplied conflict options.
     *
     * @param deletes tuples to delete
     * @param options write options
     * @return OpenFGA write response values
     */
    public Uni<Map<String, Object>> delete(Collection<RelTupleDefinition> deletes, WriteOptions options) {
        return write(List.of(), deletes, options);
    }

    public Uni<Map<String, Object>> write(@Nullable Collection<RelTupleDefinition> writes,
            @Nullable Collection<? extends RelTupleKeyed> deletes) {
        return write(writes, deletes, WriteOptions.DEFAULT);
    }

    /**
     * Writes and deletes tuples using the supplied conflict options.
     *
     * @param writes tuples to write
     * @param deletes tuples to delete
     * @param options write options
     * @return OpenFGA write response values
     */
    public Uni<Map<String, Object>> write(@Nullable Collection<RelTupleDefinition> writes,
            @Nullable Collection<? extends RelTupleKeyed> deletes, WriteOptions options) {
        return config.flatMap(config -> {
            var request = WriteRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .onDuplicate(options.onDuplicate.orElse(null))
                    .onMissing(options.onMissing.orElse(null));
            if (writes != null && !writes.isEmpty()) {
                request.writes(new WriteRequest.Writes(writes));
            }
            if (deletes != null && !deletes.isEmpty()) {
                request.deletes(new WriteRequest.Deletes(deletes));
            }
            return api.write(config.getStoreId(), request.build());
        }).map(WriteResponse::values);
    }

    public AssertionsClient assertions() {
        return new AssertionsClient(api, config);
    }

}
