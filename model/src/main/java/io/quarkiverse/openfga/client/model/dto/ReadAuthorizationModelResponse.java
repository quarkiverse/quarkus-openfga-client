package io.quarkiverse.openfga.client.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.AuthorizationModel;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ReadAuthorizationModelResponse(@JsonProperty("authorization_model") AuthorizationModel authorizationModel) {

    public ReadAuthorizationModelResponse {
        Preconditions.parameterNonNull(authorizationModel, "authorizationModel");
    }

}
