package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.RelTupleKeyed;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

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

        public WriteRequest build() {
            return new WriteRequest(writes, deletes, authorizationModelId);
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

    @JsonCreator(mode = PROPERTIES)
    WriteRequest(@Nullable Writes writes, @Nullable Deletes deletes,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId) {
        this.writes = writes;
        this.deletes = deletes;
        this.authorizationModelId = authorizationModelId;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof WriteRequest that))
            return false;
        return Objects.equals(this.writes, that.writes) &&
                Objects.equals(this.deletes, that.deletes) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(writes, deletes, authorizationModelId);
    }

    @Override
    public String toString() {
        return "WriteBody[" +
                "writes=" + writes + ", " +
                "deletes=" + deletes + ", " +
                "authorizationModelId=" + authorizationModelId + ']';
    }

}
