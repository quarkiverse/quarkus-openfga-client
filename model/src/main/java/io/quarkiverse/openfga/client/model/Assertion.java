package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Assertion {

    @JsonProperty("tuple_key")
    private final TupleKey tupleKey;

    private final boolean expectation;

    @JsonProperty("contextual_tuples")
    @Nullable
    private final List<TupleKey> contextualTuples;

    Assertion(@JsonProperty("tuple_key") TupleKey tupleKey, boolean expectation,
            @JsonProperty("contextual_tuples") @Nullable List<TupleKey> contextualTuples) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.expectation = expectation;
        this.contextualTuples = contextualTuples;
    }

    public Assertion of(TupleKey tupleKey, boolean expectation, @Nullable List<TupleKey> contextualTuples) {
        return new Assertion(tupleKey, expectation, contextualTuples);
    }

    @JsonProperty("tuple_key")
    public TupleKey getTupleKey() {
        return tupleKey;
    }

    public boolean getExpectation() {
        return expectation;
    }

    @JsonProperty("contextual_tuples")
    @Nullable
    public List<TupleKey> getContextualTuples() {
        return contextualTuples;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Assertion) obj;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                this.expectation == that.expectation &&
                Objects.equals(this.contextualTuples, that.contextualTuples);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, expectation, contextualTuples);
    }

    @Override
    public String toString() {
        return "Assertion[" +
                "tupleKey=" + tupleKey + ", " +
                "expectation=" + expectation + ", " +
                "contextualTuples=" + contextualTuples + ']';
    }

}
