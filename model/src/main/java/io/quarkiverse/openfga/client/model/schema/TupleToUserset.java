package io.quarkiverse.openfga.client.model.schema;

import java.util.List;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record TupleToUserset(String tupleset, List<Computed> computed) {

    public TupleToUserset {
        Preconditions.parameterNonNull(tupleset, "tupleset");
        Preconditions.parameterNonNull(computed, "computed");
    }

}
