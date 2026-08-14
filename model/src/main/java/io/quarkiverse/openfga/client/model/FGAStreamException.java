package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Reports an error embedded in an OpenFGA streaming response.
 */
public final class FGAStreamException extends FGAException {

    @Nullable
    private final Integer code;
    private final List<Map<String, Object>> details;

    /**
     * Creates a streaming response exception.
     *
     * @param code optional status code
     * @param message optional status message
     * @param details status details
     */
    public FGAStreamException(@Nullable Integer code, @Nullable String message, List<Map<String, Object>> details) {
        super(message == null || message.isBlank() ? "OpenFGA stream failed" : message);
        this.code = code;
        this.details = List.copyOf(details);
    }

    /**
     * Returns the status code supplied by OpenFGA.
     *
     * @return status code, or {@code null}
     */
    @Nullable
    public Integer getCode() {
        return code;
    }

    /**
     * Returns immutable status detail objects.
     *
     * @return status details
     */
    public List<Map<String, Object>> getDetails() {
        return details;
    }
}
