package io.quarkiverse.openfga.client.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record WriteAuthorizationModelResponse(@JsonProperty("authorization_model_id") String authorizationModelId) {

    public WriteAuthorizationModelResponse {
        Preconditions.parameterNonNull(authorizationModelId, "authorizationModelId");
    }

}
