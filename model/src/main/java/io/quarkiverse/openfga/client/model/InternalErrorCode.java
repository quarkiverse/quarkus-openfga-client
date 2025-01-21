package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InternalErrorCode {

    NO_INTERNAL_ERROR("no_internal_error"),

    INTERNAL_ERROR("internal_error"),

    DEADLINE_EXCEEDED("deadline_exceeded"),

    ALREADY_EXISTS("already_exists"),

    RESOURCE_EXHAUSTED("resource_exhausted"),

    FAILED_PRECONDITION("failed_precondition"),

    ABORTED("aborted"),

    OUT_OF_RANGE("out_of_range"),

    UNAVAILABLE("unavailable"),

    DATA_LOSS("data_loss"),

    UNKNOWN("unknown");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    InternalErrorCode(String name) {
        this.value = name;
    }

    @JsonCreator
    public static InternalErrorCode fromValue(String value) {
        for (InternalErrorCode e : InternalErrorCode.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
