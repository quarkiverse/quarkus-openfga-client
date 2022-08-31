package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.ContextualTupleKeys;
import io.quarkiverse.openfga.client.model.TupleKey;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class CheckBody {
    @JsonProperty("tuple_key")
    private final TupleKey tupleKey;
    @JsonProperty("contextual_tuples")
    @Nullable
    private final ContextualTupleKeys contextualTupleKeys;
    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;
    @Nullable
    private final Boolean trace;

    @JsonCreator(mode = PROPERTIES)
    public CheckBody(@JsonProperty("tuple_key") TupleKey tupleKey,
            @JsonProperty("contextual_tuples") @Nullable ContextualTupleKeys contextualTupleKeys,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId, @Nullable Boolean trace) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.contextualTupleKeys = contextualTupleKeys;
        this.authorizationModelId = authorizationModelId;
        this.trace = trace;
    }

    @JsonProperty("tuple_key")
    public TupleKey getTupleKey() {
        return tupleKey;
    }

    @JsonProperty("contextual_tuples")
    @Nullable
    public ContextualTupleKeys getContextualTupleKeys() {
        return contextualTupleKeys;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (CheckBody) obj;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                Objects.equals(this.contextualTupleKeys, that.contextualTupleKeys) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.trace, that.trace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, contextualTupleKeys, authorizationModelId, trace);
    }

    @Override
    public String toString() {
        return "CheckBody[" +
                "tupleKey=" + tupleKey + ", " +
                "contextualTupleKeys=" + contextualTupleKeys + ", " +
                "authorizationModelId=" + authorizationModelId + ", " +
                "trace=" + trace + ']';
    }

}
