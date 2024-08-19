package io.quarkiverse.openfga.client.model;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;
import static java.lang.String.format;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGANotFoundException extends FGAException {

    public enum Code {
        @JsonProperty("no_not_found_error")
        NO_NOT_FOUND_ERROR,

        @JsonProperty("undefined_endpoint")
        UNDEFINED_ENDPOINT,

        @JsonProperty("store_id_not_found")
        STORE_ID_NOT_FOUND,

        @JsonProperty("unimplemented")
        UNIMPLEMENTED,
    }

    private final Code code;

    @JsonCreator(mode = PROPERTIES)
    public FGANotFoundException(@JsonProperty("code") Code code, @JsonProperty("message") @Nullable String message) {
        super(format("%s (%s)", message, code.name().toLowerCase()));
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}
