package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotFoundErrorCode {

    NO_NOT_FOUND_ERROR("no_not_found_error"),

    UNDEFINED_ENDPOINT("undefined_endpoint"),

    STORE_ID_NOT_FOUND("store_id_not_found"),

    UNIMPLEMENTED("unimplemented"),

    UNKNOWN("unknown"),

    ;

    private final String value;

    NotFoundErrorCode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotFoundErrorCode fromValue(String value) {
        for (var e : NotFoundErrorCode.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
