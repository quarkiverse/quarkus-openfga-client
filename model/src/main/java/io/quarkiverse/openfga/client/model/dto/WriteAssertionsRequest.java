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

    public static final class Builder {

        @Nullable
        private String authorizationModelId;
        @Nullable
        private List<Assertion> assertions;

        private Builder() {
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder assertions(List<Assertion> assertions) {
            this.assertions = assertions;
            return this;
        }

        public WriteAssertionsRequest build() {
            return new WriteAssertionsRequest(
                    Preconditions.parameterNonBlank(authorizationModelId, "authorizationModelId"),
                    Preconditions.parameterNonNull(assertions, "assertions"));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final String authorizationModelId;
    private final List<Assertion> assertions;

    @JsonCreator(mode = PROPERTIES)
    WriteAssertionsRequest(@JsonProperty("authorization_model_id") String authorizationModelId, List<Assertion> assertions) {
        this.authorizationModelId = authorizationModelId;
        this.assertions = assertions;
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
        if (!(obj instanceof WriteAssertionsRequest that))
            return false;
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
