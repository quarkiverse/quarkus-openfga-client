package io.quarkiverse.openfga.client.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Assertion {

    public static Assertion of(RelTupleKeyed tupleKey, boolean expectation,
            @Nullable List<RelTupleKeyed> contextualTuples, @Nullable Map<String, Object> context) {
        return new Assertion(tupleKey, expectation, contextualTuples, context);
    }

    public static Assertion of(RelTupleKeyed tupleKey, boolean expectation) {
        return new Assertion(tupleKey, expectation, null, null);
    }

    private final RelTupleKeyed tupleKey;
    private final boolean expectation;
    @Nullable
    private final Collection<RelTupleKeyed> contextualTuples;
    private final Map<String, Object> context;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Assertion(@JsonProperty("tuple_key") RelTupleKeyed tupleKey, boolean expectation,
            @JsonProperty("contextual_tuples") @Nullable Collection<RelTupleKeyed> contextualTuples,
            @Nullable Map<String, Object> context) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.expectation = expectation;
        this.contextualTuples = contextualTuples;
        this.context = context;
    }

    @JsonProperty("tuple_key")
    @JsonSerialize(typing = JsonSerialize.Typing.STATIC)
    public RelTupleKeyed getTupleKey() {
        return tupleKey;
    }

    public boolean getExpectation() {
        return expectation;
    }

    @JsonProperty("contextual_tuples")
    @JsonSerialize(typing = JsonSerialize.Typing.STATIC)
    @Nullable
    public Collection<RelTupleKeyed> getContextualTuples() {
        return contextualTuples;
    }

    @Nullable
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Assertion that))
            return false;
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
