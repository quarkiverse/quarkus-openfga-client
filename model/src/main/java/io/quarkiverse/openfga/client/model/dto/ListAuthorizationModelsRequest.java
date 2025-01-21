package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ListAuthorizationModelsRequest {

    public static final class Builder {

        @Nullable
        private Integer pageSize;
        @Nullable
        private String continuationToken;

        private Builder() {
        }

        public Builder pageSize(@Nullable Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder continuationToken(@Nullable String continuationToken) {
            this.continuationToken = continuationToken;
            return this;
        }

        public ListAuthorizationModelsRequest build() {
            return new ListAuthorizationModelsRequest(pageSize, continuationToken);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final Integer pageSize;
    @Nullable
    private final String continuationToken;

    @JsonCreator(mode = PROPERTIES)
    ListAuthorizationModelsRequest(@JsonProperty("page_size") @Nullable Integer pageSize,
            @JsonProperty("continuation_token") @Nullable String continuationToken) {
        this.pageSize = pageSize;
        this.continuationToken = continuationToken;
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
        if (!(obj instanceof ListAuthorizationModelsRequest that))
            return false;
        return Objects.equals(this.pageSize, that.pageSize) &&
                Objects.equals(this.continuationToken, that.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageSize, continuationToken);
    }

    @Override
    public String toString() {
        return "ExpandRequest[" +
                "pageSize=" + pageSize + ", " +
                "continuationToken=" + continuationToken + ']';
    }

}
