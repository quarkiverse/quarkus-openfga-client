package io.quarkiverse.openfga.client.model.schema;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record Node(String name, @Nullable Leaf leaf, @Nullable Computed computed, TupleToUserset tupleToUserset,
        @Nullable Difference difference, @Nullable Nodes union, @Nullable Nodes intersection) {

    public Node {
        Preconditions.parameterNonNull(name, "name");
        Preconditions.oneOfNonNull("Node must have exactly one of leaf, difference, union, intersection",
                leaf, difference, union, intersection);
    }

}
