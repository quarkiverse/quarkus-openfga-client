package io.quarkiverse.openfga.client.model.dto;

import io.quarkiverse.openfga.client.model.schema.UsersetTree;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ExpandResponse(UsersetTree tree) {

    public ExpandResponse {
        Preconditions.parameterNonNull(tree, "tree");
    }

}
