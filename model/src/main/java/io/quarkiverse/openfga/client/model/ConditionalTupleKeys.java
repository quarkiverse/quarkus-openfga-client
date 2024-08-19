package io.quarkiverse.openfga.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ConditionalTupleKeys {

    private final List<ConditionalTupleKey> tupleKeys;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ConditionalTupleKeys(@JsonProperty("tuple_keys") List<ConditionalTupleKey> tupleKeys) {
        if (tupleKeys.isEmpty()) {
            throw new IllegalStateException("tupleKeys requires a minimum of 1 item");
        }
        this.tupleKeys = Preconditions.parameterNonNull(tupleKeys, "tupleKeys");
    }

    public static ConditionalTupleKeys of(@Nullable List<ConditionalTupleKey> tupleKeys) {
        if (tupleKeys == null || tupleKeys.isEmpty()) {
            return null;
        }
        return new ConditionalTupleKeys(tupleKeys);
    }

    public static final class Builder {
        private List<ConditionalTupleKey> tupleKeys;

        public Builder() {
        }

        public Builder tupleKeys(@Nullable List<ConditionalTupleKey> tupleKeys) {
            this.tupleKeys = tupleKeys;
            return this;
        }

        public Builder add(ConditionalTupleKey tupleKey) {
            if (this.tupleKeys == null) {
                this.tupleKeys = new ArrayList<>();
            }
            this.tupleKeys.add(tupleKey);
            return this;
        }

        public ConditionalTupleKeys build() {
            return new ConditionalTupleKeys(tupleKeys);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("tuple_keys")
    public List<ConditionalTupleKey> getTupleKeys() {
        return tupleKeys;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ConditionalTupleKeys) obj;
        return Objects.equals(this.tupleKeys, that.tupleKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKeys);
    }

    @Override
    public String toString() {
        return "TupleKeys[" +
                "tupleKeys=" + tupleKeys + ']';
    }

}
