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
import io.quarkiverse.openfga.client.model.schema.User;
import io.quarkiverse.openfga.client.model.schema.UsersetTree;
import io.quarkiverse.openfga.client.model.utils.Preconditions;
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
                .map(ReadAuthorizationModelResponse::authorizationModel);
    }

    public record CheckOptions(Optional<Collection<? extends RelTupleKeyed>> contextualTuples,
            Optional<Map<String, Object>> context, Optional<ConsistencyPreference> consistency) {

        public static final CheckOptions DEFAULT = new CheckOptions();

        private CheckOptions() {
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
        return config
                .flatMap(config -> {
                    var request = CheckRequest.builder()
                            .authorizationModelId(config.getAuthorizationModelId())
                            .tupleKey(relKey)
                            .contextualTuples(options.contextualTuples.map(RelTupleKeys::of).orElse(null))
                            .context(options.context.orElse(null))
                            .consistency(options.consistency.orElse(null))
                            .build();
                    return api.check(config.getStoreId(), request);
                })
                .map(CheckResponse::allowed);
    }

    public record BatchCheckOptions(Optional<ConsistencyPreference> consistency) {

        public static final BatchCheckOptions DEFAULT = new BatchCheckOptions();

        private BatchCheckOptions() {
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

        private ExpandOptions() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static ExpandOptions withContextualTuples(@Nullable Collection<? extends RelTupleKeyed> contextualTuples) {
            return new ExpandOptions(Optional.ofNullable(contextualTuples), Optional.empty(), Optional.empty());
        }

        public static ExpandOptions withContextualTuples(RelTupleKeyed... contextualTuples) {
            return withContextualTuples(List.of(contextualTuples));
        }

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

        public ExpandOptions context(@Nullable Map<String, Object> context) {
            return new ExpandOptions(contextualTuples, Optional.ofNullable(context), consistency);
        }

        public ExpandOptions consistency(@Nullable ConsistencyPreference consistency) {
            return new ExpandOptions(contextualTuples, context, Optional.ofNullable(consistency));
        }
    }

    public Uni<UsersetTree> expand(RelTupleKeyed tupleKey) {
        return expand(tupleKey, ExpandOptions.DEFAULT);
    }

    public Uni<UsersetTree> expand(RelPartialTupleKeyed tupleKey, ExpandOptions options) {
        return config.flatMap(config -> {
            var request = ExpandRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .tupleKey(tupleKey)
                    .contextualTuples(options.contextualTuples.map(RelTupleKeys::of).orElse(null))
                    .context(options.context.orElse(null))
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

    public record ListObjectsFilter(Optional<String> type, Optional<String> relation, Optional<RelUser> user) {

        private static ListObjectsFilter empty() {
            return new ListObjectsFilter(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static ListObjectsFilter byObjectType(String type) {
            return empty().objectType(type);
        }

        public static ListObjectsFilter byObjectType(RelTyped type) {
            return empty().objectType(type);
        }

        public static ListObjectsFilter byRelation(String relation) {
            return empty().relation(relation);
        }

        public static ListObjectsFilter byUser(RelUser user) {
            return empty().user(user);
        }

        public static ListObjectsFilter byUser(String user) {
            return empty().user(user);
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

        public ListObjectsFilter user(RelUser user) {
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
        Preconditions.parameterNonNull(filter, "filter");
        var type = Preconditions.parameterNonNull(filter.type, "filter.type");
        var relation = Preconditions.parameterNonNull(filter.relation, "filter.relation");
        var user = Preconditions.parameterNonNull(filter.user, "filter.user");
        return config.flatMap(config -> {
            var request = ListObjectsRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .type(type)
                    .relation(relation)
                    .user(user)
                    .contextualTuples(options.contextualTuples.orElse(null))
                    .context(options.context.orElse(null))
                    .consistency(options.consistency.orElse(null))
                    .build();
            return api.listObjects(config.getStoreId(), request);
        }).map(ListObjectsResponse::objects);
    }

    public record ListUsersFilter(Optional<RelObject> object, Optional<String> relation,
            Optional<Collection<ListUsersRequest.UserTypeFilter>> userFilters) {

        private static ListUsersFilter empty() {
            return new ListUsersFilter(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static ListUsersFilter byObject(RelObject object) {
            return empty().object(object);
        }

        public static ListUsersFilter byObject(String object) {
            return empty().object(object);
        }

        public static ListUsersFilter byRelation(String relation) {
            return empty().relation(relation);
        }

        public static ListUsersFilter byUserFilters(Collection<ListUsersRequest.UserTypeFilter> userFilters) {
            return empty().userFilters(userFilters);
        }

        public static ListUsersFilter byUserFilters(ListUsersRequest.UserTypeFilter... userFilter) {
            return empty().userFilters(userFilter);
        }

        public static ListUsersFilter byUserType(String type) {
            return empty().userType(type);
        }

        public static ListUsersFilter byUserType(RelTyped type) {
            return empty().userType(type);
        }

        public ListUsersFilter object(RelObject object) {
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

    public Uni<Collection<User>> listUsers(ListUsersFilter filter) {
        return listUsers(filter, ListOptions.DEFAULT);
    }

    public Uni<Collection<User>> listUsers(ListUsersFilter filter, ListOptions options) {
        Preconditions.parameterNonNull(filter, "filter");
        return config.flatMap(config -> {
            var request = ListUsersRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .object(filter.object.orElseThrow())
                    .relation(filter.relation.orElseThrow())
                    .userFilters(filter.userFilters.orElseThrow())
                    .contextualTuples(options.contextualTuples.orElse(null))
                    .context(options.context.orElse(null))
                    .consistency(options.consistency.orElse(null))
                    .build();
            return api.listUsers(config.getStoreId(), request);
        }).map(ListUsersResponse::users);
    }

    public record ReadFilter(Optional<RelTyped> typeOrObject, Optional<String> relation, Optional<RelUser> user) {

        public static final ReadFilter ALL = new ReadFilter(Optional.empty(), Optional.empty(), Optional.empty());

        public static ReadFilter byObjectType(@Nullable RelTyped type) {
            return ReadFilter.ALL.objectType(type);
        }

        public static ReadFilter byObjectType(@Nullable String type) {
            return ReadFilter.ALL.objectType(type);
        }

        public static ReadFilter byObject(@Nullable RelObject object) {
            return ReadFilter.ALL.object(object);
        }

        public static ReadFilter byObject(@Nullable String object) {
            return ReadFilter.ALL.object(object);
        }

        public static ReadFilter byRelation(@Nullable String relation) {
            return ReadFilter.ALL.relation(relation);
        }

        public static ReadFilter byUser(@Nullable RelUser user) {
            return ReadFilter.ALL.user(user);
        }

        public static ReadFilter byUser(@Nullable String user) {
            return ReadFilter.ALL.user(user);
        }

        public ReadFilter objectType(@Nullable RelTyped type) {
            return new ReadFilter(Optional.ofNullable(type), relation, user);
        }

        public ReadFilter objectType(@Nullable String type) {
            return new ReadFilter(Optional.ofNullable(type).map(RelObject::valueOf), relation, user);
        }

        public ReadFilter object(@Nullable RelObject object) {
            return new ReadFilter(Optional.ofNullable(object), relation, user);
        }

        public ReadFilter object(@Nullable String object) {
            return new ReadFilter(Optional.ofNullable(object).map(RelObject::valueOf), relation, user);
        }

        public ReadFilter relation(@Nullable String relation) {
            return new ReadFilter(typeOrObject, Optional.ofNullable(relation), user);
        }

        public ReadFilter user(@Nullable RelUser user) {
            return new ReadFilter(typeOrObject, relation, Optional.ofNullable(user));
        }

        public ReadFilter user(@Nullable String user) {
            return new ReadFilter(typeOrObject, relation, Optional.ofNullable(user).map(RelUser::valueOf));
        }
    }

    public Uni<PaginatedList<RelTuple>> read() {
        return read(ReadFilter.ALL, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<RelTuple>> read(ReadFilter filter) {
        return read(filter, Pagination.DEFAULT);
    }

    public Uni<PaginatedList<RelTuple>> read(Pagination options) {
        return read(ReadFilter.ALL, options);
    }

    public Uni<PaginatedList<RelTuple>> read(ReadFilter filter, Pagination options) {
        return config.flatMap(config -> {
            var request = ReadRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId())
                    .tupleKey(ReadRequest.TupleKeyFilter.builder()
                            .typeOrObject(filter.typeOrObject.orElse(null))
                            .relation(filter.relation.orElse(null))
                            .user(filter.user.orElse(null))
                            .build())
                    .pageSize(options.pageSize())
                    .continuationToken(options.continuationToken().orElse(null))
                    .build();
            return api.read(config.getStoreId(), request);
        }).map(res -> new PaginatedList<>(res.tuples(), res.continuationToken()));
    }

    public Uni<List<RelTuple>> readAll() {
        return readAll(ReadFilter.ALL, Pagination.MAX.pageSize());
    }

    public Uni<List<RelTuple>> readAll(ReadFilter filter) {
        return readAll(filter, Pagination.MAX.pageSize());
    }

    public Uni<List<RelTuple>> readAll(ReadFilter filter, @Nullable Integer pageSize) {
        return collectAllPages(pageSize, (pagination) -> this.read(filter, pagination));
    }

    public Uni<Map<String, Object>> write(RelTupleDefinition... tupleDefs) {
        return write(List.of(tupleDefs));
    }

    public Uni<Map<String, Object>> write(Collection<RelTupleDefinition> writes) {
        return write(writes, List.of());
    }

    public Uni<Map<String, Object>> delete(RelTupleDefinition... tupleDefs) {
        return delete(List.of(tupleDefs));
    }

    public Uni<Map<String, Object>> delete(Collection<RelTupleDefinition> deletes) {
        return write(List.of(), deletes);
    }

    public Uni<Map<String, Object>> write(@Nullable Collection<RelTupleDefinition> writes,
            @Nullable Collection<? extends RelTupleKeyed> deletes) {
        return config.flatMap(config -> {
            var request = WriteRequest.builder()
                    .authorizationModelId(config.getAuthorizationModelId());
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
