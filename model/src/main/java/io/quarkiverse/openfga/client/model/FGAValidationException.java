package io.quarkiverse.openfga.client.model;

import static java.lang.String.format;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGAValidationException extends FGAException {

    private final ErrorCode code;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public FGAValidationException(@JsonProperty("code") ErrorCode code,
            @JsonProperty("message") @Nullable String message) {
        super(format("%s (%s)", message, code.name().toLowerCase()));
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
