package io.quarkiverse.openfga.client.model;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;
import static java.lang.String.format;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGAInternalException extends FGAException {

    public enum Code {
        @JsonProperty("no_internal_error")
        NO_INTERNAL_ERROR,

        @JsonProperty("internal_error")
        INTERNAL_ERROR,

        @JsonProperty("cancelled")
        CANCELLED,

        @JsonProperty("deadline_exceeded")
        DEADLINE_EXCEEDED,

        @JsonProperty("already_exists")
        ALREADY_EXISTS,

        @JsonProperty("resource_exhausted")
        RESOURCE_EXHAUSTED,

        @JsonProperty("failed_precondition")
        FAILED_PRECONDITION,

        @JsonProperty("aborted")
        ABORTED,

        @JsonProperty("out_of_range")
        OUT_OF_RANGE,

        @JsonProperty("unavailable")
        UNAVAILABLE,

        @JsonProperty("data_loss")
        DATA_LOSS,
    }

    private final Code code;

    @JsonCreator(mode = PROPERTIES)
    public FGAInternalException(@JsonProperty("code") Code code, @JsonProperty("message") @Nullable String message) {
        super(format("%s (%s)", message, code.name().toLowerCase()));
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}
