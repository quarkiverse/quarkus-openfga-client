package io.quarkiverse.openfga.client.model.dto;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ListStoresRequest {

    @JsonProperty("page_size")
    @Nullable
    private final Integer pageSize;

    @JsonProperty("continuation_token")
    @Nullable
    private final String continuationToken;

    @JsonProperty("name")
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

    public static ListStoresRequest of(@Nullable Integer pageSize, @Nullable String continuationToken, @Nullable String name) {
        return new ListStoresRequest(pageSize, continuationToken, name);
    }

    public static final class Builder {
        private Integer pageSize;
        private String continuationToken;
        private String name;

        Builder() {
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
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ListStoresRequest) obj;
        return Objects.equals(this.pageSize, that.pageSize) &&
                Objects.equals(this.continuationToken, that.continuationToken) &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(pageSize, continuationToken, name);
    }

    @Override
    public String toString() {
        return "ListStoresRequest[" +
                "pageSize=" + pageSize + ", " +
                "continuationToken=" + continuationToken + ", "
                + "name=" + name + ']';
    }

}
