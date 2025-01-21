package io.quarkiverse.openfga.client.model.dto;

import java.time.OffsetDateTime;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ReadChangesRequest {

    public static final class Builder {

        @Nullable
        private String type;
        @Nullable
        private Integer pageSize;
        @Nullable
        private String continuationToken;
        @Nullable
        private OffsetDateTime startTime;

        private Builder() {
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

        public Builder startTime(@Nullable OffsetDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ReadChangesRequest build() {
            return new ReadChangesRequest(type, pageSize, continuationToken, startTime);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final String type;
    @Nullable
    private final Integer pageSize;
    @Nullable
    private final String continuationToken;
    @Nullable
    private final OffsetDateTime startTime;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ReadChangesRequest(@Nullable String type, @JsonProperty("page_size") @Nullable Integer pageSize,
            @JsonProperty("continuation_token") @Nullable String continuationToken,
            @JsonProperty("start_time") @Nullable OffsetDateTime startTime) {
        this.type = type;
        this.pageSize = pageSize;
        this.continuationToken = continuationToken;
        this.startTime = startTime;
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

    @JsonProperty("start_time")
    @Nullable
    public OffsetDateTime getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ReadChangesRequest that))
            return false;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.pageSize, that.pageSize) &&
                Objects.equals(this.continuationToken, that.continuationToken) &&
                Objects.equals(this.startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pageSize, continuationToken, startTime);
    }

    @Override
    public String toString() {
        return "ReadChangesRequest[" +
                "type=" + type + ", " +
                "pageSize=" + pageSize + ", " +
                "continuationToken=" + continuationToken + ", " +
                "startTime=" + startTime + ']';
    }

}
