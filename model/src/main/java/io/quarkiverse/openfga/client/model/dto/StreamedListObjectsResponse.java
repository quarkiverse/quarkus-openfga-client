package io.quarkiverse.openfga.client.model.dto;

import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * A single object emitted by the OpenFGA streamed list-objects operation.
 *
 * @param object related object
 */
public record StreamedListObjectsResponse(RelObject object) {

    public StreamedListObjectsResponse {
        Preconditions.parameterNonNull(object, "object");
    }
}
