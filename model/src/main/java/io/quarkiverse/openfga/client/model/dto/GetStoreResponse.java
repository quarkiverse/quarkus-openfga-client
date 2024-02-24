package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.time.OffsetDateTime;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class GetStoreResponse {
    private final String id;
    private final String name;
    @JsonProperty("created_at")
    private final OffsetDateTime createdAt;
    @JsonProperty("updated_at")
    private final OffsetDateTime updatedAt;
    @JsonProperty("deleted_at")
    @Nullable
    private final OffsetDateTime deletedAt;

    @JsonCreator(mode = PROPERTIES)
    public GetStoreResponse(String id, String name, @JsonProperty("created_at") OffsetDateTime createdAt,
            @JsonProperty("updated_at") OffsetDateTime updatedAt,
            @JsonProperty("deleted_at") @Nullable OffsetDateTime deletedAt) {
        this.id = Preconditions.parameterNonNull(id, "id");
        this.name = Preconditions.parameterNonNull(name, "name");
        this.createdAt = Preconditions.parameterNonNull(createdAt, "createdAt");
        this.updatedAt = Preconditions.parameterNonNull(updatedAt, "updatedAt");
        this.deletedAt = deletedAt;
    }

    public Store asStore() {
        return new Store(id, name, createdAt, updatedAt, null);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("created_at")
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("updated_at")
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("deleted_at")
    @Nullable
    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (GetStoreResponse) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.createdAt, that.createdAt) &&
                Objects.equals(this.updatedAt, that.updatedAt) &&
                Objects.equals(this.deletedAt, that.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdAt, updatedAt, deletedAt);
    }

    @Override
    public String toString() {
        return "GetStoreResponse[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "createdAt=" + createdAt + ", " +
                "updatedAt=" + updatedAt + ", " +
                "deletedAt=" + deletedAt + ']';
    }

}
