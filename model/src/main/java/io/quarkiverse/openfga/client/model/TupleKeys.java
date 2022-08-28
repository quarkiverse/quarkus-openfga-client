package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TupleKeys {
    private final List<TupleKey> tupleKeys;

    public TupleKeys(@JsonProperty("tuple_keys") List<TupleKey> tupleKeys) {
        if (tupleKeys.isEmpty()) {
            throw new IllegalStateException("tupleKeys requires a minimum of 1 item");
        }
        this.tupleKeys = Preconditions.parameterNonNull(tupleKeys, "tupleKeys");
        ;
    }

    public static TupleKeys of(@Nullable List<TupleKey> tupleKeys) {
        if (tupleKeys == null || tupleKeys.isEmpty()) {
            return null;
        }
        return new TupleKeys(tupleKeys);
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
        var that = (TupleKeys) obj;
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
