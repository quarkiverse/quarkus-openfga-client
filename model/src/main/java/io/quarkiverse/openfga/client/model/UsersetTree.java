package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.nodes.Node;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class UsersetTree {
    private final Node root;

    @JsonCreator
    public UsersetTree(Node root) {
        this.root = Preconditions.parameterNonNull(root, "root");
    }

    public Node getRoot() {
        return root;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (UsersetTree) obj;
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
