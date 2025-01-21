package io.quarkiverse.openfga.client.model;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * A check to be performed on a tuple.
 * <br>
 * The check is defined by a tuple key, and optionally by contextual tuples and additional context.
 * <br>
 * The contextual tuples are used to provide additional context to the check, and are used to resolve
 * the parameters of the check.
 * <br>
 * The additional context is used to provide additional data to the check, and is used to resolve
 * the parameters of the check.
 */
public record Check(@JsonProperty("tuple_key") RelTupleKeyed tupleKey,
        @JsonProperty("contextual_tuples") @Nullable RelTupleKeys contextualTuples,
        @Nullable Map<String, Object> context,
        @JsonProperty("correlation_id") String correlationId) {

    public static class Builder {

        @Nullable
        private RelTupleKeyed tupleKey;
        @Nullable
        private RelTupleKeys contextualTuples;
        @Nullable
        private Map<String, Object> context;
        @Nullable
        private String correlationId;

        public Builder tupleKey(RelTupleKeyed tupleKey) {
            this.tupleKey = tupleKey;
            return this;
        }

        public Builder contextualTuples(RelTupleKeys contextualTuples) {
            this.contextualTuples = contextualTuples;
            return this;
        }

        public Builder context(Map<String, Object> context) {
            this.context = context;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Check build() {
            return new Check(
                    Preconditions.parameterNonNull(tupleKey, "tupleKey"),
                    contextualTuples, context,
                    Preconditions.parameterNonBlank(correlationId, "correlationId"));
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
