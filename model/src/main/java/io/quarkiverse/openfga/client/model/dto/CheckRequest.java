package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.ContextualTupleKeys;
import io.quarkiverse.openfga.client.model.TupleKey;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class CheckRequest {

    @JsonProperty("tuple_key")
    private final TupleKey tupleKey;

    @JsonProperty("contextual_tuples")
    @Nullable
    private final ContextualTupleKeys contextualTuples;

    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;

    @Nullable
    private final Boolean trace;

    @Nullable
    private final Object context;

    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = PROPERTIES)
    CheckRequest(@JsonProperty("tuple_key") TupleKey tupleKey,
            @JsonProperty("contextual_tuples") @Nullable ContextualTupleKeys contextualTuples,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId, @Nullable Boolean trace,
            @Nullable Object context, @Nullable ConsistencyPreference consistency) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.contextualTuples = contextualTuples;
        this.authorizationModelId = authorizationModelId;
        this.trace = trace;
        this.context = context;
        this.consistency = consistency;
    }

    public static CheckRequest of(TupleKey tupleKey, @Nullable ContextualTupleKeys contextualTuples,
            @Nullable String authorizationModelId, @Nullable Boolean trace,
            @Nullable Object context, @Nullable ConsistencyPreference consistency) {
        return new CheckRequest(tupleKey, contextualTuples, authorizationModelId, trace, context, consistency);
    }

    public static final class Builder {
        private TupleKey tupleKey;
        private ContextualTupleKeys contextualTuples;
        private String authorizationModelId;
        private Boolean trace;
        private Object context;
        private ConsistencyPreference consistency;

        Builder() {
        }

        public Builder tupleKey(TupleKey tupleKey) {
            this.tupleKey = tupleKey;
            return this;
        }

        public Builder contextualTuples(@Nullable ContextualTupleKeys contextualTuples) {
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

        public Builder context(@Nullable Object context) {
            this.context = context;
            return this;
        }

        public Builder consistency(@Nullable ConsistencyPreference consistency) {
            this.consistency = consistency;
            return this;
        }

        public CheckRequest build() {
            return new CheckRequest(tupleKey, contextualTuples, authorizationModelId, trace, context, consistency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("tuple_key")
    public TupleKey getTupleKey() {
        return tupleKey;
    }

    @JsonProperty("contextual_tuples")
    @Nullable
    public ContextualTupleKeys getContextualTuples() {
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
        var that = (CheckRequest) obj;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                Objects.equals(this.contextualTuples, that.contextualTuples) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.trace, that.trace) &&
                Objects.equals(this.context, that.context) &&
                Objects.equals(this.consistency, that.consistency);
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
