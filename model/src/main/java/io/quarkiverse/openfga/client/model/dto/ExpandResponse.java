package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.UsersetTree;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ExpandResponse {
    private final UsersetTree tree;

    @JsonCreator(mode = PROPERTIES)
    public ExpandResponse(UsersetTree tree) {
        this.tree = Preconditions.parameterNonNull(tree, "tree");
    }

    public UsersetTree getTree() {
        return tree;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ExpandResponse) obj;
        return Objects.equals(this.tree, that.tree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tree);
    }

    @Override
    public String toString() {
        return "ExpandResponse[" +
                "tree=" + tree + ']';
    }

}
