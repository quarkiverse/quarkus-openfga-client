package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Controls how OpenFGA handles duplicate writes or deletes of missing tuples.
 */
public enum WriteConflictBehavior {

    ERROR("error"),

    IGNORE("ignore"),

    UNKNOWN("unknown");

    private final String value;

    WriteConflictBehavior(String value) {
        this.value = value;
    }

    /**
     * Returns the OpenFGA wire value.
     *
     * @return wire value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Resolves an OpenFGA wire value.
     *
     * @param value wire value
     * @return matching behavior, or {@link #UNKNOWN}
     */
    @JsonCreator
    public static WriteConflictBehavior fromValue(String value) {
        for (var behavior : values()) {
            if (behavior.value.equals(value)) {
                return behavior;
            }
        }
        return UNKNOWN;
    }
}
