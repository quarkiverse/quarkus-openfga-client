package io.quarkiverse.openfga.client.model;

import static java.lang.String.format;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGAAuthException extends FGAException {

    private final AuthErrorCode code;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public FGAAuthException(@JsonProperty("code") AuthErrorCode code,
            @JsonProperty("message") @Nullable String message) {
        super(format("%s (%s)", message, code.name().toLowerCase()));
        this.code = code;
    }

    public AuthErrorCode getCode() {
        return code;
    }
}
