package io.quarkiverse.openfga.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class Assertions {

    private final List<Assertion> assertions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Assertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    public static Assertions of(List<Assertion> assertions) {
        return new Assertions(assertions);
    }

    public static final class Builder {
        private List<Assertion> assertions;

        public Builder() {
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
            return new Assertions(assertions);
        }
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Assertions) obj;
        return this.assertions.equals(that.assertions);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(assertions);
    }

    @Override
    public String toString() {
        return "Assertions[" +
                "assertions=" + assertions + ']';
    }
}
