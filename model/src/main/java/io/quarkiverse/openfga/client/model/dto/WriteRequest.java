package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.ConditionalTupleKey;
import io.quarkiverse.openfga.client.model.TupleKey;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class WriteRequest {

    public static final class Writes {

        @JsonProperty("tuple_keys")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private final List<ConditionalTupleKey> tupleKeys;

        @JsonCreator(mode = PROPERTIES)
        Writes(@JsonProperty("tuple_keys") List<ConditionalTupleKey> tupleKeys) {
            this.tupleKeys = Preconditions.parameterNonNull(tupleKeys, "tupleKeys");
        }

        public static Writes of(@Nullable List<ConditionalTupleKey> tupleKeys) {
            if (tupleKeys == null) {
                return null;
            }
            return new Writes(tupleKeys);
        }

        public static final class Builder {
            private List<ConditionalTupleKey> tupleKeys;

            public Builder() {
            }

            public Builder addTupleKeys(List<ConditionalTupleKey> tupleKeys) {
                if (this.tupleKeys == null) {
                    this.tupleKeys = new ArrayList<>();
                }
                this.tupleKeys.addAll(tupleKeys);
                return this;
            }

            public Builder addTupleKey(ConditionalTupleKey tupleKey) {
                if (this.tupleKeys == null) {
                    this.tupleKeys = new ArrayList<>();
                }
                this.tupleKeys.add(tupleKey);
                return this;
            }

            public Writes build() {
                return new Writes(tupleKeys);
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        public List<ConditionalTupleKey> getTupleKeys() {
            return tupleKeys;
        }
    }

    public static final class Deletes {

        @JsonProperty("tuple_keys")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private final List<TupleKey> tupleKeys;

        Deletes(@JsonProperty("tuple_keys") List<TupleKey> tupleKeys) {
            this.tupleKeys = tupleKeys;
        }

        public static Deletes of(@Nullable List<TupleKey> tupleKeys) {
            if (tupleKeys == null) {
                return null;
            }
            return new Deletes(tupleKeys);
        }

        public static final class Builder {
            private List<TupleKey> tupleKeys;

            public Builder() {
            }

            public Builder addTupleKeys(List<TupleKey> tupleKeys) {
                if (this.tupleKeys == null) {
                    this.tupleKeys = new ArrayList<>();
                }
                this.tupleKeys.addAll(tupleKeys);
                return this;
            }

            public Builder addTupleKey(TupleKey tupleKey) {
                if (this.tupleKeys == null) {
                    this.tupleKeys = new ArrayList<>();
                }
                this.tupleKeys.add(tupleKey);
                return this;
            }

            public Deletes build() {
                return new Deletes(tupleKeys);
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        public List<TupleKey> getTupleKeys() {
            return tupleKeys;
        }
    }

    @Nullable
    private final Writes writes;

    @Nullable
    private final Deletes deletes;

    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;

    @JsonCreator(mode = PROPERTIES)
    WriteRequest(@Nullable Writes writes, @Nullable Deletes deletes,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId) {
        this.writes = writes;
        this.deletes = deletes;
        this.authorizationModelId = authorizationModelId;
    }

    public static WriteRequest of(@Nullable Writes writes, @Nullable Deletes deletes, @Nullable String authorizationModelId) {
        return new WriteRequest(writes, deletes, authorizationModelId);
    }

    public static final class Builder {
        private Writes writes;
        private Deletes deletes;
        private String authorizationModelId;

        public Builder() {
        }

        public Builder writes(@Nullable Writes writes) {
            this.writes = writes;
            return this;
        }

        public Builder addWrites(List<ConditionalTupleKey> tupleKeys) {
            if (this.writes == null) {
                this.writes = Writes.of(new ArrayList<>());
            }
            this.writes.getTupleKeys().addAll(tupleKeys);
            return this;
        }

        public Builder addWrite(ConditionalTupleKey tupleKey) {
            if (this.writes == null) {
                this.writes = Writes.of(new ArrayList<>());
            }
            this.writes.getTupleKeys().add(tupleKey);
            return this;
        }

        public Builder deletes(@Nullable Deletes deletes) {
            this.deletes = deletes;
            return this;
        }

        public Builder addDeletes(List<TupleKey> tupleKeys) {
            if (this.deletes == null) {
                this.deletes = Deletes.of(new ArrayList<>());
            }
            this.deletes.getTupleKeys().addAll(tupleKeys);
            return this;
        }

        public Builder addDelete(TupleKey tupleKey) {
            if (this.deletes == null) {
                this.deletes = Deletes.of(new ArrayList<>());
            }
            this.deletes.getTupleKeys().add(tupleKey);
            return this;
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public WriteRequest build() {
            var writes = this.writes != null && this.writes.getTupleKeys().isEmpty() ? null : this.writes;
            var deletes = this.deletes != null && this.deletes.getTupleKeys().isEmpty() ? null : this.deletes;
            return new WriteRequest(writes, deletes, authorizationModelId);
        }
    }

    public static Builder builder() {
        return new Builder();
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
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (WriteRequest) obj;
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
