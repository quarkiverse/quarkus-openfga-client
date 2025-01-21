package io.quarkiverse.openfga.client.model.dto;

import java.util.List;

import io.quarkiverse.openfga.client.model.schema.User;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record ListUsersResponse(List<User> users) {

    public ListUsersResponse {
        Preconditions.parameterNonNull(users, "users");
    }

}
