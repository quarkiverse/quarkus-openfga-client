package io.quarkiverse.openfga.client.model;

import static java.lang.String.format;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGAUnprocessableContentException extends FGAException {

    private final UnprocessableContentErrorCode code;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public FGAUnprocessableContentException(@JsonProperty("code") UnprocessableContentErrorCode code,
            @JsonProperty("message") @Nullable String message) {
        super(format("%s (%s)", message, code.name().toLowerCase()));
        this.code = code;
    }

    public UnprocessableContentErrorCode getCode() {
        return code;
    }
}
