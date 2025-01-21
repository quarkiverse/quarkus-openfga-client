package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.RelPartialTupleKeyed;
import io.quarkiverse.openfga.client.model.RelTupleKeys;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ExpandRequest {

    public static final class Builder {

        @Nullable
        private RelPartialTupleKeyed tupleKey;
        @Nullable
        private String authorizationModelId;
        @Nullable
        private RelTupleKeys contextualTuples;
        @Nullable
        private Map<String, Object> context;
        @Nullable
        private ConsistencyPreference consistency;

        private Builder() {
        }

        public Builder tupleKey(RelPartialTupleKeyed tupleKey) {
            this.tupleKey = tupleKey;
            return this;
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder contextualTuples(@Nullable RelTupleKeys contextualTuples) {
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

        public ExpandRequest build() {
            return new ExpandRequest(
                    Preconditions.parameterNonNull(tupleKey, "tupleKey"),
                    authorizationModelId,
                    contextualTuples,
                    context,
                    consistency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final RelPartialTupleKeyed tupleKey;
    @Nullable
    private final String authorizationModelId;
    @Nullable
    private final RelTupleKeys contextualTuples;
    @Nullable
    private final Map<String, Object> context;
    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = PROPERTIES)
    ExpandRequest(@JsonProperty("tuple_key") RelPartialTupleKeyed tupleKey,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @JsonProperty("contextual_tuples") @Nullable RelTupleKeys contextualTuples,
            @JsonProperty("context") @Nullable Map<String, Object> context,
            @Nullable ConsistencyPreference consistency) {
        this.tupleKey = tupleKey;
        this.authorizationModelId = authorizationModelId;
        this.contextualTuples = contextualTuples;
        this.context = context;
        this.consistency = consistency;
    }

    @JsonProperty("tuple_key")
    @JsonSerialize(typing = JsonSerialize.Typing.STATIC)
    public RelPartialTupleKeyed getTupleKey() {
        return tupleKey;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
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
        if (!(obj instanceof ExpandRequest that))
            return false;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.consistency, that.consistency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, authorizationModelId, consistency);
    }

    @Override
    public String toString() {
        return "ExpandRequest[" +
                "tupleKey=" + tupleKey + ", " +
                "authorizationModelId=" + authorizationModelId + ", " +
                "consistency=" + consistency + ']';
    }

}
