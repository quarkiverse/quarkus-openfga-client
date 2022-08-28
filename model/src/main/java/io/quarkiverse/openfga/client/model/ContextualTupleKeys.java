package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ContextualTupleKeys {
    @JsonProperty("tuple_keys")
    private final List<TupleKey> tupleKeys;

    public ContextualTupleKeys(@JsonProperty("tuple_keys") List<TupleKey> tupleKeys) {
        this.tupleKeys = Preconditions.parameterNonNull(tupleKeys, "tupleKeys");
    }

    @JsonProperty("tuple_keys")
    public List<TupleKey> getTupleKeys() {
        return tupleKeys;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ContextualTupleKeys) obj;
        return Objects.equals(this.tupleKeys, that.tupleKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKeys);
    }

    @Override
    public String toString() {
        return "ContextualTupleKeys[" +
                "tupleKeys=" + tupleKeys + ']';
    }

}
