package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ListChangesRequest {

    @Nullable
    private final String type;

    @JsonProperty("page_size")
    @Nullable
    private final Integer pageSize;

    @JsonProperty("continuation_token")
    @Nullable
    private final String continuationToken;

    @JsonCreator(mode = PROPERTIES)
    ListChangesRequest(@Nullable String type, @JsonProperty("page_size") @Nullable Integer pageSize,
            @JsonProperty("continuation_token") @Nullable String continuationToken) {
        this.type = type;
        this.pageSize = pageSize;
        this.continuationToken = continuationToken;
    }

    public static ListChangesRequest of(@Nullable String type, @Nullable Integer pageSize, @Nullable String continuationToken) {
        return new ListChangesRequest(type, pageSize, continuationToken);
    }

    public static final class Builder {
        private String type;
        private Integer pageSize;
        private String continuationToken;

        public Builder() {
        }

        public Builder type(@Nullable String type) {
            this.type = type;
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

        public ListChangesRequest build() {
            return new ListChangesRequest(type, pageSize, continuationToken);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    public String getType() {
        return type;
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
        var that = (ListChangesRequest) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.pageSize, that.pageSize) &&
                Objects.equals(this.continuationToken, that.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pageSize, continuationToken);
    }

    @Override
    public String toString() {
        return "ExpandRequest[" +
                "type=" + type + ", " +
                "pageSize=" + pageSize + ", " +
                "continuationToken=" + continuationToken + ']';
    }

}
