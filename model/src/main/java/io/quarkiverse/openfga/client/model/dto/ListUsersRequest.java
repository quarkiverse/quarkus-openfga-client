package io.quarkiverse.openfga.client.model.dto;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.RelTupleKeyed;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ListUsersRequest {

    public static final class UserTypeFilter {

        public static final class Builder {

            @Nullable
            private String type;
            @Nullable
            private String relation;

            private Builder() {
            }

            public Builder type(String type) {
                this.type = type;
                return this;
            }

            public Builder relation(@Nullable String relation) {
                this.relation = relation;
                return this;
            }

            public UserTypeFilter build() {
                return new UserTypeFilter(
                        Preconditions.parameterNonBlank(type, "type"),
                        relation);
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        private final String type;
        @Nullable
        private final String relation;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        UserTypeFilter(String type, @Nullable String relation) {
            this.type = type;
            this.relation = relation;
        }

        public String getType() {
            return type;
        }

        @Nullable
        public String getRelation() {
            return relation;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof UserTypeFilter that))
                return false;
            return type.equals(that.type) &&
                    Objects.equals(relation, that.relation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, relation);
        }

        @Override
        public String toString() {
            return "UserTypeFilter[" +
                    "type='" + type + "', " +
                    "relation='" + relation + ']';
        }

    }

    public static final class Builder {

        @Nullable
        private String authorizationModelId;
        @Nullable
        private RelObject object;
        @Nullable
        private String relation;
        @Nullable
        private Collection<UserTypeFilter> userFilters;
        @Nullable
        private Collection<? extends RelTupleKeyed> contextualTuples;
        @Nullable
        private Map<String, Object> context;
        @Nullable
        private ConsistencyPreference consistency;

        private Builder() {
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder object(RelObject object) {
            this.object = object;
            return this;
        }

        public Builder relation(String relation) {
            this.relation = relation;
            return this;
        }

        public Builder userFilters(Collection<UserTypeFilter> userFilters) {
            this.userFilters = userFilters;
            return this;
        }

        public Builder contextualTuples(@Nullable Collection<? extends RelTupleKeyed> contextualTuples) {
            this.contextualTuples = contextualTuples;
            return this;
        }

        public Builder context(@Nullable Map<String, Object> context) {
            this.context = context;
            return this;
        }

        public Builder consistency(@Nullable ConsistencyPreference consistency) {
            this.consistency = consistency;
            return this;
        }

        public ListUsersRequest build() {
            return new ListUsersRequest(authorizationModelId,
                    Preconditions.parameterNonNull(this.object, "object"),
                    Preconditions.parameterNonNull(this.relation, "relation"),
                    Preconditions.parameterNonNull(this.userFilters, "userFilters"),
                    contextualTuples, context, consistency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final String authorizationModelId;
    private final RelObject object;
    private final String relation;
    private final Collection<UserTypeFilter> userFilters;
    @Nullable
    private final Collection<? extends RelTupleKeyed> contextualTuples;
    @Nullable
    private final Map<String, Object> context;
    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ListUsersRequest(@JsonProperty("authorization_model_id") @Nullable String authorizationModelId, RelObject object,
            String relation, @JsonProperty("user_filters") Collection<UserTypeFilter> userFilters,
            @JsonProperty("contextual_tuples") @Nullable Collection<? extends RelTupleKeyed> contextualTuples,
            @Nullable Map<String, Object> context, @Nullable ConsistencyPreference consistency) {
        this.authorizationModelId = authorizationModelId;
        this.object = object;
        this.relation = relation;
        this.userFilters = userFilters;
        this.contextualTuples = contextualTuples;
        this.context = context;
        this.consistency = consistency;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public RelObject getObject() {
        return object;
    }

    public String getRelation() {
        return relation;
    }

    @JsonProperty("user_filters")
    public Collection<UserTypeFilter> getUserFilters() {
        return userFilters;
    }

    @JsonProperty("contextual_tuples")
    @Nullable
    public Collection<? extends RelTupleKeyed> getContextualTuples() {
        return contextualTuples;
    }

    @Nullable
    public Map<String, Object> getContext() {
        return context;
    }

    @Nullable
    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ListUsersRequest that))
            return false;
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
