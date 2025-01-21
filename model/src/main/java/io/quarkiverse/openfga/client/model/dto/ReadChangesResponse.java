package io.quarkiverse.openfga.client.model.dto;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.RelTupleChange;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ReadChangesResponse(List<RelTupleChange> changes,
        @JsonProperty("continuation_token") @Nullable String continuationToken) {

    public ReadChangesResponse {
        Preconditions.parameterNonNull(changes, "changes");
    }

}
