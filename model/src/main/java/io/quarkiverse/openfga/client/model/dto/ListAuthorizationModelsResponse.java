package io.quarkiverse.openfga.client.model.dto;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.AuthorizationModel;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ListAuthorizationModelsResponse(
        @JsonProperty("authorization_models") List<AuthorizationModel> authorizationModels,
        @JsonProperty("continuation_token") @Nullable String continuationToken) {

    public ListAuthorizationModelsResponse {
        Preconditions.parameterNonNull(authorizationModels, "authorizationModels");
    }

}
