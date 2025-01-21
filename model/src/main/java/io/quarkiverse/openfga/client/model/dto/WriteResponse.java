package io.quarkiverse.openfga.client.model.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record WriteResponse(@JsonValue Map<String, Object> values) {

    public WriteResponse {
        Preconditions.parameterNonNull(values, "values");
    }

}
