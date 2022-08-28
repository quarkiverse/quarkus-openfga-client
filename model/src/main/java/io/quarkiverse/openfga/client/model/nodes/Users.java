package io.quarkiverse.openfga.client.model.nodes;

import java.util.List;
import java.util.Objects;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Users {
    private final List<String> users;

    public Users(List<String> users) {
        this.users = Preconditions.parameterNonNull(users, "users");
    }

    public List<String> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Users) obj;
        return Objects.equals(this.users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }

    @Override
    public String toString() {
        return "Users[" +
                "users=" + users + ']';
    }

}
