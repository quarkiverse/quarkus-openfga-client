package io.quarkiverse.openfga.client.model.dto;

import java.util.List;

import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ListObjectsResponse(List<RelObject> objects) {

    public ListObjectsResponse {
        Preconditions.parameterNonNull(objects, "objects");
    }

}
