package io.quarkiverse.openfga.client.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class RelTuple {

    public static RelTuple of(RelTupleDefinition key, OffsetDateTime timestamp) {
        return new RelTuple(key, timestamp);
    }

    private final RelTupleDefinition key;
    private final OffsetDateTime timestamp;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelTuple(RelTupleDefinition key, OffsetDateTime timestamp) {
        this.key = Preconditions.parameterNonNull(key, "key");
        this.timestamp = Preconditions.parameterNonNull(timestamp, "timestamp");
    }

    public RelTupleDefinition getKey() {
        return key;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof RelTuple that))
            return false;
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
