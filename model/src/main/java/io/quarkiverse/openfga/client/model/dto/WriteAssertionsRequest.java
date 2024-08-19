package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class WriteAssertionsRequest {

    @JsonProperty("authorization_model_id")
    private final String authorizationModelId;

    private final List<Assertion> assertions;

    @JsonCreator(mode = PROPERTIES)
    WriteAssertionsRequest(@JsonProperty("authorization_model_id") String authorizationModelId, List<Assertion> assertions) {
        this.authorizationModelId = Preconditions.parameterNonNull(authorizationModelId, "authorizationModelId");
        this.assertions = Preconditions.parameterNonNull(assertions, "assertions");
    }

    public static WriteAssertionsRequest of(String authorizationModelId, List<Assertion> assertions) {
        return new WriteAssertionsRequest(authorizationModelId, assertions);
    }

    public static final class Builder {
        private String authorizationModelId;
        private List<Assertion> assertions;

        public Builder() {
        }

        public Builder authorizationModelId(String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
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
            if (assertions == null) {
                assertions = new java.util.ArrayList<>();
            }
            assertions.add(assertion);
            return this;
        }

        public WriteAssertionsRequest build() {
            return new WriteAssertionsRequest(authorizationModelId, assertions);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("authorization_model_id")
    public String getAuthorizationModelId() {
        return authorizationModelId;
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
        return Objects.equals(this.assertions, that.assertions) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assertions, authorizationModelId);
    }

    @Override
    public String toString() {
        return "WriteAssertionsRequest[" +
                "assertions=" + assertions + ", " +
                "authorizationModelId=" + authorizationModelId + ']';
    }

}
