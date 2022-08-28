package io.quarkiverse.openfga.client.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Tuple {
    private final TupleKey key;
    private final OffsetDateTime timestamp;

    public Tuple(TupleKey key, OffsetDateTime timestamp) {
        this.key = Preconditions.parameterNonNull(key, "key");
        this.timestamp = Preconditions.parameterNonNull(timestamp, "timestamp");
    }

    public TupleKey getKey() {
        return key;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
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
