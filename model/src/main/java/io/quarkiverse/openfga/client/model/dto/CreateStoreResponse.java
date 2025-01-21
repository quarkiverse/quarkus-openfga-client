package io.quarkiverse.openfga.client.model.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record CreateStoreResponse(String id, String name, @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("updated_at") OffsetDateTime updatedAt) {

    public CreateStoreResponse {
        Preconditions.parameterNonNull(id, "id");
        Preconditions.parameterNonNull(name, "name");
        Preconditions.parameterNonNull(createdAt, "createdAt");
        Preconditions.parameterNonNull(updatedAt, "updatedAt");
    }

    public Store asStore() {
        return Store.of(id, name, createdAt, updatedAt, null);
    }

}
