package io.quarkiverse.openfga.client.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CredentialsFlowResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("scope") String scope,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Integer expiresIn) {
}
