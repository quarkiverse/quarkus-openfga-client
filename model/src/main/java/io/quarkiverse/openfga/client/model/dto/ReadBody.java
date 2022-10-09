package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.PartialTupleKey;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ReadBody {
    @JsonProperty("tuple_key")
    private final PartialTupleKey tupleKey;
    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;
    @JsonProperty("page_size")
    @Nullable
    private final Integer pageSize;
    @JsonProperty("continuation_token")
    @Nullable
    private final String continuationToken;

    @JsonCreator(mode = PROPERTIES)
    public ReadBody(@JsonProperty("tuple_key") PartialTupleKey tupleKey,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @JsonProperty("page_size") @Nullable Integer pageSize,
            @JsonProperty("continuation_token") @Nullable String continuationToken) {
        this.tupleKey = Preconditions.parameterNonNull(tupleKey, "tupleKey");
        this.authorizationModelId = authorizationModelId;
        this.pageSize = pageSize;
        this.continuationToken = continuationToken;
    }

    @JsonProperty("tuple_key")
    public PartialTupleKey getTupleKey() {
        return tupleKey;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @JsonProperty("page_size")
    @Nullable
    public Integer getPageSize() {
        return pageSize;
    }

    @JsonProperty("continuation_token")
    @Nullable
    public String getContinuationToken() {
        return continuationToken;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ReadBody) obj;
        return Objects.equals(this.tupleKey, that.tupleKey) &&
                Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.pageSize, that.pageSize) &&
                Objects.equals(this.continuationToken, that.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, authorizationModelId, pageSize, continuationToken);
    }

    @Override
    public String toString() {
        return "ReadBody[" +
                "tupleKey=" + tupleKey + ", " +
                "authorizationModelId=" + authorizationModelId + ", " +
                "pageSize=" + pageSize + ", " +
                "continuationToken=" + continuationToken + ']';
    }

}
