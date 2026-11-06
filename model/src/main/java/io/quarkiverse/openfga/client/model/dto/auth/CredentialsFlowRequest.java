package io.quarkiverse.openfga.client.model.dto.auth;

import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.ModelMapper;
import tools.jackson.core.type.TypeReference;

public record CredentialsFlowRequest(
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret,
        @JsonProperty("audience") URI audience,
        @JsonProperty("scope") String scope,
        @JsonProperty("grant_type") String grantType) {

    public Map<String, String> toForm() {
        return ModelMapper.mapper.convertValue(this, new TypeReference<Map<String, String>>() {
        });
    }

}
