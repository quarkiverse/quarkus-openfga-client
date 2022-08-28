package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Assertion {
    private final TupleKey tupleKey;
    private final boolean expectation;

    public Assertion(@JsonProperty("tuple_key") TupleKey tupleKey, boolean expectation) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.expectation = expectation;
    }

    @JsonProperty("tuple_key")
    public TupleKey getTupleKey() {
        return tupleKey;
    }

    public boolean getExpectation() {
        return expectation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Assertion) obj;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                this.expectation == that.expectation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, expectation);
    }

    @Override
    public String toString() {
        return "Assertion[" +
                "tupleKey=" + tupleKey + ", " +
                "expectation=" + expectation + ']';
    }

}
