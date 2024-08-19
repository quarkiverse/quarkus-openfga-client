package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.*;

public final class ListUsersRequest {

    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;

    private final AnyObject object;

    private final String relation;

    @JsonProperty("user_filters")
    private final List<UserTypeFilter> userFilters;

    @JsonProperty("contextual_tuples")
    @Nullable
    private final ContextualTupleKeys contextualTuples;

    @Nullable
    private final Object context;

    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = PROPERTIES)
    ListUsersRequest(@JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            AnyObject object, String relation, List<UserTypeFilter> userFilters,
            @Nullable @JsonProperty("contextual_tuples") ContextualTupleKeys contextualTuples,
            @Nullable Object context, @Nullable ConsistencyPreference consistency) {
        this.authorizationModelId = authorizationModelId;
        this.object = object;
        this.relation = relation;
        this.userFilters = userFilters;
        this.contextualTuples = contextualTuples;
        this.context = context;
        this.consistency = consistency;
    }

    public static ListUsersRequest of(@Nullable String authorizationModelId, AnyObject object, String relation,
            List<UserTypeFilter> userFilters, @Nullable ContextualTupleKeys contextualTuples,
            @Nullable Object context, @Nullable ConsistencyPreference consistency) {
        return new ListUsersRequest(authorizationModelId, object, relation, userFilters, contextualTuples, context,
                consistency);
    }

    public static final class Builder {
        private String authorizationModelId;
        private AnyObject object;
        private String relation;
        private List<UserTypeFilter> userFilters;
        private ContextualTupleKeys contextualTuples;
        private Object context;
        private ConsistencyPreference consistency;

        public Builder() {
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder object(AnyObject object) {
            this.object = object;
            return this;
        }

        public Builder relation(String relation) {
            this.relation = relation;
            return this;
        }

        public Builder userFilters(List<UserTypeFilter> userFilters) {
            this.userFilters = userFilters;
            return this;
        }

        public Builder addUserFilters(List<UserTypeFilter> userFilters) {
            if (this.userFilters == null) {
                this.userFilters = new ArrayList<>();
            }
            this.userFilters.addAll(userFilters);
            return this;
        }

        public Builder addUserFilter(UserTypeFilter userFilter) {
            if (this.userFilters == null) {
                this.userFilters = new ArrayList<>();
            }
            this.userFilters.add(userFilter);
            return this;
        }

        public Builder contextualTuples(@Nullable ContextualTupleKeys contextualTuples) {
            this.contextualTuples = contextualTuples;
            return this;
        }

        public Builder addContextualTuples(List<ConditionalTupleKey> contextualTuples) {
            if (this.contextualTuples == null) {
                this.contextualTuples = ContextualTupleKeys.of(new ArrayList<>());
            }
            this.contextualTuples.getTupleKeys().addAll(contextualTuples);
            return this;
        }

        public Builder addContextualTuple(ConditionalTupleKey contextualTuple) {
            if (contextualTuples == null) {
                contextualTuples = ContextualTupleKeys.of(new ArrayList<>());
            }
            contextualTuples.getTupleKeys().add(contextualTuple);
            return this;
        }

        public Builder context(@Nullable Object context) {
            this.context = context;
            return this;
        }

        public Builder consistency(@Nullable ConsistencyPreference consistency) {
            this.consistency = consistency;
            return this;
        }

        public ListUsersRequest build() {
            return new ListUsersRequest(authorizationModelId, object, relation, userFilters, contextualTuples, context,
                    consistency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public AnyObject getObject() {
        return object;
    }

    public String getRelation() {
        return relation;
    }

    @JsonProperty("user_filters")
    public List<UserTypeFilter> getUserFilters() {
        return userFilters;
    }

    @JsonProperty("contextual_tuples")
    @Nullable
    public ContextualTupleKeys getContextualTuples() {
        return contextualTuples;
    }

    @Nullable
    public Object getContext() {
        return context;
    }

    @Nullable
    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ListUsersRequest) obj;
        return Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.object, that.object) &&
                Objects.equals(this.relation, that.relation) &&
                Objects.equals(this.userFilters, that.userFilters) &&
                Objects.equals(this.contextualTuples, that.contextualTuples) &&
                Objects.equals(this.context, that.context) &&
                Objects.equals(this.consistency, that.consistency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModelId, object, relation, userFilters, contextualTuples, context, consistency);
    }

    @Override
    public String toString() {
        return "ListObjectsRequest[" +
                "authorizationModelId=" + authorizationModelId + ", " +
                "object=" + object + ", " +
                "relation=" + relation + ", " +
                "userFilters=" + userFilters + ", " +
                "contextualTupleKeys=" + contextualTuples + ", " +
                "context=" + context + ", " +
                "consistency=" + consistency + ']';
    }

}
