package io.quarkiverse.openfga.client.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.ConditionalTupleKey;
import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.ContextualTupleKeys;

public final class ListObjectsRequest {

    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;

    private final String type;

    private final String relation;

    private final String user;

    @JsonProperty("contextual_tuples")
    @Nullable
    private final ContextualTupleKeys contextualTuples;

    @Nullable
    private final Object context;

    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ListObjectsRequest(@JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            String type, String relation, String user,
            @Nullable @JsonProperty("contextual_tuples") ContextualTupleKeys contextualTuples,
            @Nullable Object context, @Nullable ConsistencyPreference consistency) {
        this.authorizationModelId = authorizationModelId;
        this.type = type;
        this.relation = relation;
        this.user = user;
        this.contextualTuples = contextualTuples;
        this.context = context;
        this.consistency = consistency;
    }

    public static ListObjectsRequest of(@Nullable String authorizationModelId, String type, String relation, String user,
            @Nullable ContextualTupleKeys contextualTuples, @Nullable Object context,
            @Nullable ConsistencyPreference consistency) {
        return new ListObjectsRequest(authorizationModelId, type, relation, user, contextualTuples, context, consistency);
    }

    public static final class Builder {
        private String authorizationModelId;
        private String type;
        private String relation;
        private String user;
        private ContextualTupleKeys contextualTuples;
        private Object context;
        private ConsistencyPreference consistency;

        public Builder() {
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

        public Builder user(String user) {
            this.user = user;
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

        public ListObjectsRequest build() {
            return new ListObjectsRequest(authorizationModelId, type, relation, user, contextualTuples, context, consistency);
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

    public String getType() {
        return type;
    }

    public String getRelation() {
        return relation;
    }

    public String getUser() {
        return user;
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
        var that = (ListObjectsRequest) obj;
        return Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.relation, that.relation) &&
                Objects.equals(this.user, that.user) &&
                Objects.equals(this.contextualTuples, that.contextualTuples) &&
                Objects.equals(this.context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModelId, type, relation, user, contextualTuples, context);
    }

    @Override
    public String toString() {
        return "ListObjectsRequest[" +
                "authorizationModelId=" + authorizationModelId + ", " +
                "type=" + type + ", " +
                "relation=" + relation + ", " +
                "user=" + user + ", " +
                "contextualTupleKeys=" + contextualTuples + ", " +
                "context=" + context + ']';
    }

}
