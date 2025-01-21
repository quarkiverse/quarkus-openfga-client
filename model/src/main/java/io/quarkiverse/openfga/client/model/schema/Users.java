package io.quarkiverse.openfga.client.model.schema;

import java.util.List;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record Users(List<String> users) {

    public Users {
        Preconditions.parameterNonNull(users, "users");
    }

}
