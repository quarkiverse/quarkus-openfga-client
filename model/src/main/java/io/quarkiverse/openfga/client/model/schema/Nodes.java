package io.quarkiverse.openfga.client.model.schema;

import java.util.List;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record Nodes(List<Node> nodes) {

    public Nodes {
        Preconditions.parameterNonNull(nodes, "nodes");
    }

}
