package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthErrorCode {

    NO_AUTH_ERROR("no_auth_error"),

    AUTH_FAILED_INVALID_SUBJECT("auth_failed_invalid_subject"),

    AUTH_FAILED_INVALID_AUDIENCE("auth_failed_invalid_audience"),

    AUTH_FAILED_INVALID_ISSUER("auth_failed_invalid_issuer"),

    INVALID_CLAIMS("invalid_claims"),

    AUTH_FAILED_INVALID_BEARER_TOKEN("auth_failed_invalid_bearer_token"),

    BEARER_TOKEN_MISSING("bearer_token_missing"),

    UNAUTHENTICATED("unauthenticated"),

    FORBIDDEN("forbidden"),

    UNKNOWN("unknown"),

    ;

    private final String value;

    AuthErrorCode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AuthErrorCode fromValue(String value) {
        for (var e : AuthErrorCode.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
