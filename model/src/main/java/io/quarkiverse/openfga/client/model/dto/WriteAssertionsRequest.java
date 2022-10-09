package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class WriteAssertionsRequest {
    private final List<Assertion> assertions;

    @JsonCreator(mode = PROPERTIES)
    public WriteAssertionsRequest(List<Assertion> assertions) {
        this.assertions = Preconditions.parameterNonNull(assertions, "assertions");
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (WriteAssertionsRequest) obj;
        return Objects.equals(this.assertions, that.assertions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assertions);
    }

    @Override
    public String toString() {
        return "WriteAssertionsRequest[" +
                "assertions=" + assertions + ']';
    }

}
