package io.quarkiverse.openfga.client.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TupleChange {

    private final ConditionalTupleKey tupleKey;

    private final TupleOperation operation;

    private final OffsetDateTime timestamp;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    TupleChange(@JsonProperty("tuple_key") ConditionalTupleKey tupleKey, TupleOperation operation,
            OffsetDateTime timestamp) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.operation = Preconditions.parameterNonNull(operation, "operation");
        this.timestamp = Preconditions.parameterNonNull(timestamp, "timestamp");
    }

    public static TupleChange of(ConditionalTupleKey tupleKey, TupleOperation operation, OffsetDateTime timestamp) {
        return new TupleChange(tupleKey, operation, timestamp);
    }

    @JsonProperty("tuple_key")
    public ConditionalTupleKey getTupleKey() {
        return tupleKey;
    }

    public TupleOperation getOperation() {
        return operation;
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
        var that = (TupleChange) obj;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                Objects.equals(this.operation, that.operation) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, operation, timestamp);
    }

    @Override
    public String toString() {
        return "TupleChange[" +
                "tupleKey=" + tupleKey + ", " +
                "operation=" + operation + ", " +
                "timestamp=" + timestamp + ']';
    }

}
