package io.quarkiverse.openfga.client.model.dto;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.RelTupleKeyed;
import io.quarkiverse.openfga.client.model.RelTupleKeys;
import io.quarkiverse.openfga.client.model.RelUser;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ListObjectsRequest {

    public static final class Builder {

        @Nullable
        private String authorizationModelId;
        @Nullable
        private String type;
        @Nullable
        private String relation;
        @Nullable
        private RelUser user;
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

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder relation(String relation) {
            this.relation = relation;
            return this;
        }

        public Builder user(RelUser user) {
            this.user = user;
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

        public ListObjectsRequest build() {
            var type = Preconditions.parameterNonBlank(this.type, "type");
            var relation = Preconditions.parameterNonBlank(this.relation, "relation");
            var user = Preconditions.parameterNonNull(this.user, "user");
            return new ListObjectsRequest(authorizationModelId, type, relation, user,
                    RelTupleKeys.of(contextualTuples), context, consistency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final String authorizationModelId;
    private final String type;
    private final String relation;
    private final RelUser user;
    @Nullable
    private final RelTupleKeys contextualTuples;
    @Nullable
    private final Map<String, Object> context;
    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ListObjectsRequest(@JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            String type, String relation, RelUser user,
            @JsonProperty("contextual_tuples") @Nullable RelTupleKeys contextualTuples,
            @Nullable Map<String, Object> context, @Nullable ConsistencyPreference consistency) {
        this.authorizationModelId = authorizationModelId;
        this.type = type;
        this.relation = relation;
        this.user = user;
        this.contextualTuples = contextualTuples;
        this.context = context;
        this.consistency = consistency;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public String getType() {
        return type;
    }

    public String getRelation() {
        return relation;
    }

    public RelUser getUser() {
        return user;
    }

    @Nullable
    public RelTupleKeys getContextualTuples() {
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
        if (!(obj instanceof ListObjectsRequest that))
            return false;
        return Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.relation, that.relation) &&
                Objects.equals(this.user, that.user) &&
                Objects.equals(this.contextualTuples, that.contextualTuples) &&
                Objects.equals(this.context, that.context) &&
                Objects.equals(this.consistency, that.consistency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModelId, type, relation, user, contextualTuples, context, consistency);
    }

    @Override
    public String toString() {
        return "ListObjectsRequest[" +
                "authorizationModelId=" + authorizationModelId + ", " +
                "type=" + type + ", " +
                "relation=" + relation + ", " +
                "user=" + user + ", " +
                "contextualTupleKeys=" + contextualTuples + ", " +
                "context=" + context + ", " +
                "consistency=" + consistency + ']';
    }

}
