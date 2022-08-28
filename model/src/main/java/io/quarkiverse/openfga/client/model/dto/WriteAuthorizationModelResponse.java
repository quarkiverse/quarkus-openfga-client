package io.quarkiverse.openfga.client.model.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class WriteAuthorizationModelResponse {
    @JsonProperty("authorization_model_id")
    private final String authorizationModelId;

    public WriteAuthorizationModelResponse(@JsonProperty("authorization_model_id") String authorizationModelId) {
        this.authorizationModelId = Preconditions.parameterNonNull(authorizationModelId, "authorizationModelId");
    }

    @JsonProperty("authorization_model_id")
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (WriteAuthorizationModelResponse) obj;
        return Objects.equals(this.authorizationModelId, that.authorizationModelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModelId);
    }

    @Override
    public String toString() {
        return "WriteAuthorizationModelResponse[" +
                "authorizationModelId=" + authorizationModelId + ']';
    }

}
