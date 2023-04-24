package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.PartialTupleKey;

public final class ReadBody {
    @JsonProperty("tuple_key")
    @Nullable
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
    public ReadBody(@JsonProperty("tuple_key") @Nullable PartialTupleKey tupleKey,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @JsonProperty("page_size") @Nullable Integer pageSize,
            @JsonProperty("continuation_token") @Nullable String continuationToken) {
        this.tupleKey = tupleKey;
        this.authorizationModelId = authorizationModelId;
        this.pageSize = pageSize;
        this.continuationToken = continuationToken;
    }

    @JsonProperty("tuple_key")
    @Nullable
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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ReadBody))
            return false;
        ReadBody readBody = (ReadBody) o;
        return Objects.equals(tupleKey, readBody.tupleKey)
                && Objects.equals(authorizationModelId, readBody.authorizationModelId)
                && Objects.equals(pageSize, readBody.pageSize) && Objects.equals(continuationToken, readBody.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, authorizationModelId, pageSize, continuationToken);
    }

    @Override
    public String toString() {
        return "ReadBody{" +
                "tupleKey=" + tupleKey +
                ", authorizationModelId='" + authorizationModelId + '\'' +
                ", pageSize=" + pageSize +
                ", continuationToken='" + continuationToken + '\'' +
                '}';
    }
}
