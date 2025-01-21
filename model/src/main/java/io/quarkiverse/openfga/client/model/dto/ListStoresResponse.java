package io.quarkiverse.openfga.client.model.dto;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ListStoresResponse(List<Store> stores, @JsonProperty("continuation_token") @Nullable String continuationToken) {

    public ListStoresResponse {
        Preconditions.parameterNonNull(stores, "stores");
    }

}
