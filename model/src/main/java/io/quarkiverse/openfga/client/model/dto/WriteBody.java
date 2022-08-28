package io.quarkiverse.openfga.client.model.dto;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.TupleKeys;

public final class WriteBody {
    @Nullable
    private final TupleKeys writes;
    @Nullable
    private final TupleKeys deletes;
    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;

    public WriteBody(@Nullable TupleKeys writes, @Nullable TupleKeys deletes,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId) {
        this.writes = writes;
        this.deletes = deletes;
        this.authorizationModelId = authorizationModelId;
    }

    @Nullable
    public TupleKeys getWrites() {
        return writes;
    }

    @Nullable
    public TupleKeys getDeletes() {
        return deletes;
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
        var that = (WriteBody) obj;
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
