package io.quarkiverse.openfga.client.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Tuple {

    private final ConditionalTupleKey key;

    private final OffsetDateTime timestamp;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Tuple(ConditionalTupleKey key, OffsetDateTime timestamp) {
        this.key = Preconditions.parameterNonNull(key, "key");
        this.timestamp = Preconditions.parameterNonNull(timestamp, "timestamp");
    }

    public static Tuple of(ConditionalTupleKey key, OffsetDateTime timestamp) {
        return new Tuple(key, timestamp);
    }

    public ConditionalTupleKey getKey() {
        return key;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Tuple) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, timestamp);
    }

    @Override
    public String toString() {
        return "Tuple[" +
                "key=" + key + ", " +
                "timestamp=" + timestamp + ']';
    }

}
