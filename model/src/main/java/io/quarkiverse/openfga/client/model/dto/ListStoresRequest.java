package io.quarkiverse.openfga.client.model.dto;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ListStoresRequest {

    public static final class Builder {

        @Nullable
        private Integer pageSize;
        @Nullable
        private String continuationToken;
        @Nullable
        private String name;

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

        public Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        public ListStoresRequest build() {
            return new ListStoresRequest(pageSize, continuationToken, name);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final Integer pageSize;
    @Nullable
    private final String continuationToken;
    @Nullable
    private final String name;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ListStoresRequest(@JsonProperty("page_size") @Nullable Integer pageSize,
            @JsonProperty("continuation_token") @Nullable String continuationToken,
            @JsonProperty("name") @Nullable String name) {
        this.pageSize = pageSize;
        this.continuationToken = continuationToken;
        this.name = name;
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

    @JsonProperty("name")
    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ListStoresRequest that))
            return false;
        return Objects.equals(pageSize, that.pageSize) &&
                Objects.equals(continuationToken, that.continuationToken) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageSize, continuationToken, name);
    }

    @Override
    public String toString() {
        return "ListStoresRequest[" +
                "pageSize=" + pageSize + ", " +
                "continuationToken=" + continuationToken + ", "
                + "name=" + name + ']';
    }

}
