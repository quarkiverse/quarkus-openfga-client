package io.quarkiverse.openfga.client.model.schema;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record Difference(Node base, Node subtract) {

    public Difference {
        Preconditions.parameterNonNull(base, "base");
        Preconditions.parameterNonNull(subtract, "subtract");
    }

}
