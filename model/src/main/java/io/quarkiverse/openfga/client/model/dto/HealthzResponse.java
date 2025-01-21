package io.quarkiverse.openfga.client.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HealthzResponse(String status) {
}
