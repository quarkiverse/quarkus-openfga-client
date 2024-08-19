package io.quarkiverse.openfga.client.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.User;

public final class ListUsersResponse {

    private final List<User> users;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ListUsersResponse(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (ListUsersResponse) o;
        return users.equals(that.users);
    }

    @Override
    public int hashCode() {
        return users.hashCode();
    }

    @Override
    public String toString() {
        return "ListUsersResponse[" +
                "users=" + users + ']';
    }

}
