package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.RelEntity;
import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.RelObjectType;
import io.quarkiverse.openfga.client.model.RelTuple;
import io.quarkiverse.openfga.client.model.RelTupleChange;
import io.quarkiverse.openfga.client.model.RelTyped;
import io.quarkiverse.openfga.client.model.RelUser;
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

    /**
     * Filters tuples read from this store.
     *
     * @param typeOrObject optional object type or concrete object
     * @param relation optional relation
     * @param user optional user
     */
    public record ReadTuplesFilter(Optional<RelTyped> typeOrObject, Optional<String> relation,
            Optional<RelEntity> user) {

        /** Matches every tuple in the store. */
        public static final ReadTuplesFilter ALL = new ReadTuplesFilter();

        /**
         * Creates a filter matching an object type.
         *
         * @param type object type
         * @return tuple filter
         */
        public static ReadTuplesFilter byObjectType(@Nullable RelTyped type) {
            return ALL.objectType(type);
        }

        /**
         * Creates a filter matching an object type.
         *
         * @param type object type
         * @return tuple filter
         */
        public static ReadTuplesFilter byObjectType(@Nullable String type) {
            return ALL.objectType(type);
        }

        /**
         * Creates a filter matching an object.
         *
         * @param object object
         * @return tuple filter
         */
        public static ReadTuplesFilter byObject(@Nullable RelEntity object) {
            return ALL.object(object);
        }

        /**
         * Creates a filter matching an object.
         *
         * @param object object
         * @return tuple filter
         */
        public static ReadTuplesFilter byObject(@Nullable String object) {
            return ALL.object(object);
        }

        /**
         * Creates a filter matching a relation.
         *
         * @param relation relation
         * @return tuple filter
         */
        public static ReadTuplesFilter byRelation(@Nullable String relation) {
            return ALL.relation(relation);
        }

        /**
         * Creates a filter matching a user.
         *
         * @param user user
         * @return tuple filter
         */
        public static ReadTuplesFilter byUser(@Nullable RelEntity user) {
            return ALL.user(user);
        }

        /**
         * Creates a filter matching a user.
         *
         * @param user user
         * @return tuple filter
         */
        public static ReadTuplesFilter byUser(@Nullable String user) {
            return ALL.user(user);
        }

        /** Creates a filter matching every tuple. */
        public ReadTuplesFilter() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        /**
         * Adds an object type constraint.
         *
         * @param type object type
         * @return updated tuple filter
         */
        public ReadTuplesFilter objectType(@Nullable RelTyped type) {
            return new ReadTuplesFilter(Optional.ofNullable(type), relation, user);
        }

        /**
         * Adds an object type constraint.
         *
         * @param type object type
         * @return updated tuple filter
         */
        public ReadTuplesFilter objectType(@Nullable String type) {
            return new ReadTuplesFilter(Optional.ofNullable(type).map(RelObjectType::of), relation, user);
        }

        /**
         * Adds an object constraint.
         *
         * @param object object
         * @return updated tuple filter
         */
        public ReadTuplesFilter object(@Nullable RelEntity object) {
            return new ReadTuplesFilter(Optional.ofNullable(object), relation, user);
        }

        /**
         * Adds an object constraint.
         *
         * @param object object
         * @return updated tuple filter
         */
        public ReadTuplesFilter object(@Nullable String object) {
            return new ReadTuplesFilter(Optional.ofNullable(object).map(RelObject::valueOf), relation, user);
        }

        /**
         * Adds a relation constraint.
         *
         * @param relation relation
         * @return updated tuple filter
         */
        public ReadTuplesFilter relation(@Nullable String relation) {
            return new ReadTuplesFilter(typeOrObject, Optional.ofNullable(relation), user);
        }

        /**
         * Adds a user constraint.
         *
         * @param user user
         * @return updated tuple filter
         */
        public ReadTuplesFilter user(@Nullable RelEntity user) {
            return new ReadTuplesFilter(typeOrObject, relation, Optional.ofNullable(user));
        }

        /**
         * Adds a user constraint.
         *
         * @param user user
         * @return updated tuple filter
         */
        public ReadTuplesFilter user(@Nullable String user) {
            return new ReadTuplesFilter(typeOrObject, relation, Optional.ofNullable(user).map(RelUser::valueOf));
        }
    }

    /**
     * Options controlling tuple reads.
     *
     * @param consistency optional consistency preference
     */
    public record ReadTuplesOptions(Optional<ConsistencyPreference> consistency) {

        /** Uses the OpenFGA server's default consistency. */
        public static final ReadTuplesOptions DEFAULT = new ReadTuplesOptions();

        /** Creates options using the OpenFGA server's default consistency. */
        public ReadTuplesOptions() {
            this(Optional.empty());
        }

        /**
         * Creates options with a consistency preference.
         *
         * @param consistency consistency preference
         * @return read options
         */
        public static ReadTuplesOptions withConsistency(@Nullable ConsistencyPreference consistency) {
            return new ReadTuplesOptions(Optional.ofNullable(consistency));
        }

        /**
         * Sets the consistency preference.
         *
         * @param consistency consistency preference
         * @return updated read options
         */
        public ReadTuplesOptions consistency(@Nullable ConsistencyPreference consistency) {
            return new ReadTuplesOptions(Optional.ofNullable(consistency));
        }
    }

    public Uni<PaginatedList<RelTuple>> readTuples() {
        return readTuples(ReadTuplesFilter.ALL, Pagination.DEFAULT, ReadTuplesOptions.DEFAULT);
    }

    public Uni<PaginatedList<RelTuple>> readTuples(Pagination pagination) {
        return readTuples(ReadTuplesFilter.ALL, pagination, ReadTuplesOptions.DEFAULT);
    }

    /**
     * Reads tuples matching a filter.
     *
     * @param filter tuple filter
     * @return a page of tuples
     */
    public Uni<PaginatedList<RelTuple>> readTuples(ReadTuplesFilter filter) {
        return readTuples(filter, Pagination.DEFAULT, ReadTuplesOptions.DEFAULT);
    }

    /**
     * Reads tuples using the supplied options.
     *
     * @param options read options
     * @return a page of tuples
     */
    public Uni<PaginatedList<RelTuple>> readTuples(ReadTuplesOptions options) {
        return readTuples(ReadTuplesFilter.ALL, Pagination.DEFAULT, options);
    }

    /**
     * Reads tuples matching a filter and pagination request.
     *
     * @param filter tuple filter
     * @param pagination pagination request
     * @return a page of tuples
     */
    public Uni<PaginatedList<RelTuple>> readTuples(ReadTuplesFilter filter, Pagination pagination) {
        return readTuples(filter, pagination, ReadTuplesOptions.DEFAULT);
    }

    /**
     * Reads tuples matching a filter, pagination request, and consistency preference.
     *
     * @param filter tuple filter
     * @param pagination pagination request
     * @param options read options
     * @return a page of tuples
     */
    public Uni<PaginatedList<RelTuple>> readTuples(ReadTuplesFilter filter, Pagination pagination,
            ReadTuplesOptions options) {
        var request = ReadRequest.builder()
                .tupleKey(ReadRequest.TupleKeyFilter.builder()
                        .typeOrObject(filter.typeOrObject.orElse(null))
                        .relation(filter.relation.orElse(null))
                        .user(filter.user.map(RelEntity::asUser).orElse(null))
                        .build())
                .pageSize(pagination.pageSize())
                .continuationToken(pagination.continuationToken().orElse(null))
                .consistency(options.consistency.orElse(null))
                .build();
        return storeId.flatMap(storeId -> api.read(storeId, request))
                .map(res -> new PaginatedList<>(res.tuples(), res.continuationToken()));
    }

    public Uni<List<RelTuple>> readAllTuples() {
        return readAllTuples((Integer) null);
    }

    public Uni<List<RelTuple>> readAllTuples(@Nullable Integer pageSize) {
        return readAllTuples(ReadTuplesFilter.ALL, pageSize, ReadTuplesOptions.DEFAULT);
    }

    /**
     * Reads all tuples matching a filter.
     *
     * @param filter tuple filter
     * @return all matching tuples
     */
    public Uni<List<RelTuple>> readAllTuples(ReadTuplesFilter filter) {
        return readAllTuples(filter, null, ReadTuplesOptions.DEFAULT);
    }

    /**
     * Reads all tuples matching a filter using the supplied page size and options.
     *
     * @param filter tuple filter
     * @param pageSize page size, or {@code null} for the default
     * @param options read options
     * @return all matching tuples
     */
    public Uni<List<RelTuple>> readAllTuples(ReadTuplesFilter filter, @Nullable Integer pageSize,
            ReadTuplesOptions options) {
        return collectAllPages(pageSize, pagination -> readTuples(filter, pagination, options));
    }

    public AuthorizationModelsClient authorizationModels() {
        return new AuthorizationModelsClient(api, storeId);
    }

}
