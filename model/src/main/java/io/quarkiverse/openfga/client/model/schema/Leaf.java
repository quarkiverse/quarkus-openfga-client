package io.quarkiverse.openfga.client.model.schema;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record Leaf(@Nullable Users users, @Nullable Computed computed, @Nullable TupleToUserset tupleToUserset) {

    public Leaf {
        Preconditions.oneOfNonNull("Leaf must have exactly one of users, computed, tupleToUserset",
                users, computed, tupleToUserset);
    }

}
