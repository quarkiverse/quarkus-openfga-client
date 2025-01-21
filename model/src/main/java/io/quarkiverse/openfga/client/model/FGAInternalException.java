package io.quarkiverse.openfga.client.model;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;
import static java.lang.String.format;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGAInternalException extends FGAException {

    private final InternalErrorCode code;

    @JsonCreator(mode = PROPERTIES)
    public FGAInternalException(@JsonProperty("code") InternalErrorCode code,
            @JsonProperty("message") @Nullable String message) {
        super(format("%s (%s)", message, code.name().toLowerCase()));
        this.code = code;
    }

    public InternalErrorCode getCode() {
        return code;
    }
}
