package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class WriteResponse {
    @JsonValue
    private final Map<String, Object> values;

    @JsonCreator(mode = DELEGATING)
    public WriteResponse(Map<String, Object> values) {
        this.values = values;
    }

    @JsonValue
    public Map<String, Object> getValues() {
        return values;
    }
}
