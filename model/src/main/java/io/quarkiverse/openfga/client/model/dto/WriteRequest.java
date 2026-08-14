package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkiverse.openfga.client.model.RelTupleKeyed;
import io.quarkiverse.openfga.client.model.WriteConflictBehavior;
import io.quarkiverse.openfga.client.model.json.WriteRequestSerializer;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

@JsonSerialize(using = WriteRequestSerializer.class)
public final class WriteRequest {

    public record Writes(
            @JsonProperty("tuple_keys") @JsonInclude(JsonInclude.Include.NON_EMPTY) Collection<? extends RelTupleKeyed> tupleKeys) {

        public Writes {
            Preconditions.parameterNonNull(tupleKeys, "tupleKeys");
        }

        public static Writes of(@Nullable Collection<? extends RelTupleKeyed> tupleKeys) {
            if (tupleKeys == null || tupleKeys.isEmpty())
                return new Writes(List.of());
            return new Writes(tupleKeys);
        }
    }

    public record Deletes(
            @JsonProperty("tuple_keys") @JsonInclude(JsonInclude.Include.NON_EMPTY) Collection<? extends RelTupleKeyed> tupleKeys) {

        public Deletes {
            Preconditions.parameterNonNull(tupleKeys, "tupleKeys");
        }

        public static Deletes of(@Nullable Collection<? extends RelTupleKeyed> tupleKeys) {
            if (tupleKeys == null || tupleKeys.isEmpty())
                return new Deletes(List.of());
            return new Deletes(tupleKeys);
        }
    }

    public static final class Builder {

        private @Nullable String authorizationModelId;
        private @Nullable Writes writes;
        private @Nullable Deletes deletes;
        private @Nullable WriteConflictBehavior onDuplicate;
        private @Nullable WriteConflictBehavior onMissing;

        private Builder() {
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder writes(@Nullable Writes writes) {
            this.writes = writes;
            return this;
        }

        public Builder deletes(@Nullable Deletes deletes) {
            this.deletes = deletes;
            return this;
        }

        /**
         * Sets the behavior when a written tuple already exists.
         *
         * @param onDuplicate duplicate handling behavior
         * @return this builder
         */
        public Builder onDuplicate(@Nullable WriteConflictBehavior onDuplicate) {
            this.onDuplicate = onDuplicate;
            return this;
        }

        /**
         * Sets the behavior when a deleted tuple does not exist.
         *
         * @param onMissing missing tuple handling behavior
         * @return this builder
         */
        public Builder onMissing(@Nullable WriteConflictBehavior onMissing) {
            this.onMissing = onMissing;
            return this;
        }

        public WriteRequest build() {
            return new WriteRequest(writes, deletes, authorizationModelId, onDuplicate, onMissing);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final Writes writes;
    @Nullable
    private final Deletes deletes;
    @Nullable
    private final String authorizationModelId;
    @Nullable
    private final WriteConflictBehavior onDuplicate;
    @Nullable
    private final WriteConflictBehavior onMissing;

    @JsonCreator(mode = PROPERTIES)
    WriteRequest(@Nullable Writes writes, @Nullable Deletes deletes,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @JsonProperty("on_duplicate") @Nullable WriteConflictBehavior onDuplicate,
            @JsonProperty("on_missing") @Nullable WriteConflictBehavior onMissing) {
        this.writes = writes;
        this.deletes = deletes;
        this.authorizationModelId = authorizationModelId;
        this.onDuplicate = onDuplicate;
        this.onMissing = onMissing;
    }

    @Nullable
    public Writes getWrites() {
        return writes;
    }

    @Nullable
    public Deletes getDeletes() {
        return deletes;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    /**
     * Returns the duplicate handling behavior.
     *
     * @return duplicate handling behavior, or {@code null} for the server default
     */
    @Nullable
    public WriteConflictBehavior getOnDuplicate() {
        return onDuplicate;
    }

    /**
     * Returns the missing tuple handling behavior.
     *
     * @return missing tuple handling behavior, or {@code null} for the server default
     */
    @Nullable
    public WriteConflictBehavior getOnMissing() {
        return onMissing;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof WriteRequest that))
            return false;
        return Objects.equals(this.writes, that.writes) &&
                Objects.equals(this.deletes, that.deletes) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.onDuplicate, that.onDuplicate) &&
                Objects.equals(this.onMissing, that.onMissing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(writes, deletes, authorizationModelId, onDuplicate, onMissing);
    }

    @Override
    public String toString() {
        return "WriteBody[" +
                "writes=" + writes + ", " +
                "deletes=" + deletes + ", " +
                "authorizationModelId=" + authorizationModelId + ", " +
                "onDuplicate=" + onDuplicate + ", " +
                "onMissing=" + onMissing + ']';
    }

}
