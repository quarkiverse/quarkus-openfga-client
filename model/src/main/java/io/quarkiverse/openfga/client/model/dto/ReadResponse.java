package io.quarkiverse.openfga.client.model.dto;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.RelTuple;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ReadResponse(List<RelTuple> tuples, @JsonProperty("continuation_token") @Nullable String continuationToken) {

    public ReadResponse {
        Preconditions.parameterNonNull(tuples, "tuples");
    }

}
