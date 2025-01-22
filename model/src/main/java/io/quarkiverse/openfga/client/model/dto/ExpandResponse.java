package io.quarkiverse.openfga.client.model.dto;

import io.quarkiverse.openfga.client.model.Schema;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ExpandResponse(Schema.UsersetTree tree) {

    public ExpandResponse {
        Preconditions.parameterNonNull(tree, "tree");
    }

}
