package io.quarkiverse.openfga.client.model.schema;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record Computed(String userset) {

    public Computed {
        Preconditions.parameterNonNull(userset, "userset");
    }

}
