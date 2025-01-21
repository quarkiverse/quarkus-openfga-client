package io.quarkiverse.openfga.client.model.dto;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.RelTupleKeyed;
import io.quarkiverse.openfga.client.model.RelTupleKeys;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class CheckRequest {

    public static final class Builder {

        @Nullable
        private RelTupleKeyed tupleKey;
        @Nullable
        private RelTupleKeys contextualTuples;
        @Nullable
        private String authorizationModelId;
        @Nullable
        private Boolean trace;
        @Nullable
        private Map<String, Object> context;
        @Nullable
        private ConsistencyPreference consistency;

        private Builder() {
        }

        public Builder tupleKey(RelTupleKeyed tupleKey) {
            this.tupleKey = tupleKey;
            return this;
        }

        public Builder contextualTuples(@Nullable RelTupleKeys contextualTuples) {
            this.contextualTuples = contextualTuples;
            return this;
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder trace(@Nullable Boolean trace) {
            this.trace = trace;
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

        public CheckRequest build() {
            return new CheckRequest(
                    Preconditions.parameterNonNull(tupleKey, "tupleKey"), contextualTuples,
                    authorizationModelId, trace, context, consistency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final RelTupleKeyed tupleKey;
    @Nullable
    private final RelTupleKeys contextualTuples;
    @Nullable
    private final String authorizationModelId;
    @Nullable
    private final Boolean trace;
    @Nullable
    private final Map<String, Object> context;
    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    CheckRequest(@JsonProperty("tuple_key") RelTupleKeyed tupleKey,
            @JsonProperty("contextual_tuples") @Nullable RelTupleKeys contextualTuples,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId, @Nullable Boolean trace,
            @Nullable Map<String, Object> context, @Nullable ConsistencyPreference consistency) {
        this.tupleKey = tupleKey;
        this.contextualTuples = contextualTuples;
        this.authorizationModelId = authorizationModelId;
        this.trace = trace;
        this.context = context;
        this.consistency = consistency;
    }

    @JsonProperty("tuple_key")
    @JsonSerialize(typing = JsonSerialize.Typing.STATIC)
    public RelTupleKeyed getTupleKey() {
        return tupleKey;
    }

    @JsonProperty("contextual_tuples")
    @Nullable
    public RelTupleKeys getContextualTuples() {
        return contextualTuples;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @Nullable
    public Boolean getTrace() {
        return trace;
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
        if (!(obj instanceof CheckRequest that))
            return false;
        return Objects.equals(tupleKey, that.tupleKey) &&
                Objects.equals(contextualTuples, that.contextualTuples) &&
                Objects.equals(authorizationModelId, that.authorizationModelId) &&
                Objects.equals(trace, that.trace) &&
                Objects.equals(context, that.context) &&
                consistency == that.consistency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, contextualTuples, authorizationModelId, trace, context, consistency);
    }

    @Override
    public String toString() {
        return "CheckRequest[" +
                "tupleKey=" + tupleKey + ", " +
                "contextualTupleKeys=" + contextualTuples + ", " +
                "authorizationModelId=" + authorizationModelId + ", " +
                "trace=" + trace + ", " +
                "context=" + context + ", " +
                "consistency=" + consistency + ']';
    }

}
