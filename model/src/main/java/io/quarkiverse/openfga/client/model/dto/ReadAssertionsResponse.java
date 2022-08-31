package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ReadAssertionsResponse {
    @JsonProperty("authorization_model_id")
    private final String authorizationModelId;
    private final List<Assertion> assertions;

    @JsonCreator(mode = PROPERTIES)
    public ReadAssertionsResponse(@JsonProperty("authorization_model_id") String authorizationModelId,
            List<Assertion> assertions) {
        this.authorizationModelId = Preconditions.parameterNonNull(authorizationModelId, "authorizationModelId");
        this.assertions = Preconditions.parameterNonNull(assertions, "assertions");
    }

    @JsonProperty("authorization_model_id")
    public String getAuthorizationModelId() {
        return authorizationModelId;
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
        var that = (ReadAssertionsResponse) obj;
        return Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.assertions, that.assertions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModelId, assertions);
    }

    @Override
    public String toString() {
        return "ReadAssertionsResponse[" +
                "authorizationModelId=" + authorizationModelId + ", " +
                "assertions=" + assertions + ']';
    }

}
