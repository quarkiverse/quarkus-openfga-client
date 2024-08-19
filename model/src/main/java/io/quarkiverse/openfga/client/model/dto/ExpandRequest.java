package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.ExpandTupleKey;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ExpandRequest {

    @JsonProperty("tuple_key")
    private final ExpandTupleKey tupleKey;

    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;

    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = PROPERTIES)
    ExpandRequest(@JsonProperty("tuple_key") ExpandTupleKey tupleKey,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @Nullable ConsistencyPreference consistency) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.authorizationModelId = authorizationModelId;
        this.consistency = consistency;
    }

    public static ExpandRequest of(ExpandTupleKey tupleKey, @Nullable String authorizationModelId,
            @Nullable ConsistencyPreference consistency) {
        return new ExpandRequest(tupleKey, authorizationModelId, consistency);
    }

    public static final class Builder {
        private ExpandTupleKey tupleKey;
        private String authorizationModelId;
        private ConsistencyPreference consistency;

        public Builder() {
        }

        public Builder tupleKey(ExpandTupleKey tupleKey) {
            this.tupleKey = tupleKey;
            return this;
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder consistency(@Nullable ConsistencyPreference consistency) {
            this.consistency = consistency;
            return this;
        }

        public ExpandRequest build() {
            return new ExpandRequest(tupleKey, authorizationModelId, consistency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("tuple_key")
    public ExpandTupleKey getTupleKey() {
        return tupleKey;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @Nullable
    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ExpandRequest) obj;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.consistency, that.consistency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, authorizationModelId);
    }

    @Override
    public String toString() {
        return "ExpandRequest[" +
                "tupleKey=" + tupleKey + ", " +
                "authorizationModelId=" + authorizationModelId + ", " +
                "consistency=" + consistency + ']';
    }

}
