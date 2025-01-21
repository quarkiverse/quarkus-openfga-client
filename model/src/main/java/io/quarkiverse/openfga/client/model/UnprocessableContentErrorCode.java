package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UnprocessableContentErrorCode {

    NO_THROTTLED_ERROR_CODE("no_throttled_error_code"),

    THROTTLED_TIMEOUT_ERROR("throttled_timeout_error"),

    UNKNOWN("unknown"),

    ;

    private final String value;

    UnprocessableContentErrorCode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UnprocessableContentErrorCode fromValue(String value) {
        for (var e : UnprocessableContentErrorCode.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
