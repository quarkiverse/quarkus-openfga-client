package io.quarkiverse.openfga.client.model;

import static java.lang.String.format;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGANotFoundException extends FGAException {

    private final NotFoundErrorCode code;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public FGANotFoundException(@JsonProperty("code") NotFoundErrorCode code,
            @JsonProperty("message") @Nullable String message) {
        super(format("%s (%s)", message, code.name().toLowerCase()));
        this.code = code;
    }

    public NotFoundErrorCode getCode() {
        return code;
    }
}
