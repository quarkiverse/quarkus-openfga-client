package io.quarkiverse.openfga.client.model.schema;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class UsersetTree {

    public static UsersetTree of(Node root) {
        return new UsersetTree(root);
    }

    private final Node root;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    UsersetTree(Node root) {
        this.root = Preconditions.parameterNonNull(root, "root");
    }

    public Node getRoot() {
        return root;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UsersetTree that))
            return false;
        return Objects.equals(this.root, that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    @Override
    public String toString() {
        return "UsersetTree[" +
                "root=" + root + ']';
    }

}
