package io.quarkiverse.openfga.client.model;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;
import static java.lang.String.format;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGAInputValidationException extends FGAException {

    private final InputErrorCode code;

    @JsonCreator(mode = PROPERTIES)
    public FGAInputValidationException(@JsonProperty("code") InputErrorCode code,
            @JsonProperty("message") @Nullable String message) {
        super(format("%s (%s)", message, code.name().toLowerCase()));
        this.code = code;
    }

    public InputErrorCode getCode() {
        return code;
    }
}
