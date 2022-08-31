package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.TupleKey;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ExpandBody {
    @JsonProperty("tuple_key")
    private final TupleKey tupleKey;
    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;

    @JsonCreator(mode = PROPERTIES)
    public ExpandBody(@JsonProperty("tuple_key") TupleKey tupleKey,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.authorizationModelId = authorizationModelId;
    }

    @JsonProperty("tuple_key")
    public TupleKey getTupleKey() {
        return tupleKey;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ExpandBody) obj;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, authorizationModelId);
    }

    @Override
    public String toString() {
        return "ExpandBody[" +
                "tupleKey=" + tupleKey + ", " +
                "authorizationModelId=" + authorizationModelId + ']';
    }

}
