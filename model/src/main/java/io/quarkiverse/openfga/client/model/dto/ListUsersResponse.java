package io.quarkiverse.openfga.client.model.dto;

import java.util.Collection;
import java.util.List;

import io.quarkiverse.openfga.client.model.RelTyped;
import io.quarkiverse.openfga.client.model.Schema;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ListUsersResponse(List<Schema.User> users) {

    public ListUsersResponse {
        Preconditions.parameterNonNull(users, "users");
    }

    public Collection<RelTyped> asRel() {
        return users.stream().map(Schema.User::asRel).toList();
    }
}
