package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Assertions {

    public static final class Builder {

        private @Nullable List<Assertion> assertions;

        private Builder() {
        }

        public Builder assertions(List<Assertion> assertions) {
            this.assertions = assertions;
            return this;
        }

        public Builder addAssertions(List<Assertion> assertions) {
            if (this.assertions == null) {
                this.assertions = new java.util.ArrayList<>();
            }
            this.assertions.addAll(assertions);
            return this;
        }

        public Builder addAssertion(Assertion assertion) {
            if (this.assertions == null) {
                this.assertions = new java.util.ArrayList<>();
            }
            this.assertions.add(assertion);
            return this;
        }

        public Assertions build() {
            return new Assertions(
                    Preconditions.parameterNonNull(assertions, "assertions"));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final List<Assertion> assertions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Assertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Assertions that))
            return false;
        return this.assertions.equals(that.assertions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assertions);
    }

    @Override
    public String toString() {
        return "Assertions[" +
                "assertions=" + assertions + ']';
    }
}
