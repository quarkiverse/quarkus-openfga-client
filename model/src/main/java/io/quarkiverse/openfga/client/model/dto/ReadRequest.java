package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.PartialTupleKey;

public final class ReadRequest {

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
    ReadRequest(@JsonProperty("tuple_key") @Nullable PartialTupleKey tupleKey,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @JsonProperty("page_size") @Nullable Integer pageSize,
            @JsonProperty("continuation_token") @Nullable String continuationToken) {
        this.tupleKey = tupleKey;
        this.authorizationModelId = authorizationModelId;
        this.pageSize = pageSize;
        this.continuationToken = continuationToken;
    }

    public static final class Builder {
        private PartialTupleKey tupleKey;
        private String authorizationModelId;
        private Integer pageSize;
        private String continuationToken;

        public Builder() {
        }

        public Builder tupleKey(@Nullable PartialTupleKey tupleKey) {
            this.tupleKey = tupleKey;
            return this;
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder pageSize(@Nullable Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder continuationToken(@Nullable String continuationToken) {
            this.continuationToken = continuationToken;
            return this;
        }

        public ReadRequest build() {
            return new ReadRequest(tupleKey, authorizationModelId, pageSize, continuationToken);
        }
    }

    public static Builder builder() {
        return new Builder();
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ReadRequest) obj;
        return Objects.equals(tupleKey, that.tupleKey)
                && Objects.equals(authorizationModelId, that.authorizationModelId)
                && Objects.equals(pageSize, that.pageSize) && Objects.equals(continuationToken, that.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, authorizationModelId, pageSize, continuationToken);
    }

    @Override
    public String toString() {
        return "ReadRequest{" +
                "tupleKey=" + tupleKey +
                ", authorizationModelId='" + authorizationModelId + '\'' +
                ", pageSize=" + pageSize +
                ", continuationToken='" + continuationToken + '\'' +
                '}';
    }
}
