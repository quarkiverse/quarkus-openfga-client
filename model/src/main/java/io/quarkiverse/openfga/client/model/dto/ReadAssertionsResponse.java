package io.quarkiverse.openfga.client.model.dto;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ReadAssertionsResponse(@JsonProperty("authorization_model_id") String authorizationModelId,
        @Nullable List<Assertion> assertions) {

    public ReadAssertionsResponse {
        Preconditions.parameterNonNull(authorizationModelId, "authorizationModelId");
    }

}
