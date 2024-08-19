package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ContextualTupleKeys {

    @JsonProperty("tuple_keys")
    private final List<ConditionalTupleKey> tupleKeys;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ContextualTupleKeys(@JsonProperty("tuple_keys") List<ConditionalTupleKey> tupleKeys) {
        if (tupleKeys.size() > 20) {
            throw new IllegalStateException("tupleKeys must have at most 20 items");
        }
        this.tupleKeys = Preconditions.parameterNonNull(tupleKeys, "tupleKeys");
    }

    public static ContextualTupleKeys of(@Nullable List<ConditionalTupleKey> tupleKeys) {
        if (tupleKeys == null) {
            return null;
        }
        return new ContextualTupleKeys(tupleKeys);
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
