package io.quarkiverse.openfga.client.model.nodes;

import java.util.Objects;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Node {
    private final String name;
    @Nullable
    private final Leaf leaf;
    @Nullable
    private final Difference difference;
    @Nullable
    private final Nodes union;
    @Nullable
    private final Nodes intersection;

    public Node(String name, @Nullable Leaf leaf, @Nullable Difference difference, @Nullable Nodes union,
            @Nullable Nodes intersection) {
        this.name = Preconditions.parameterNonNull(name, "name");
        this.leaf = leaf;
        this.difference = difference;
        this.union = union;
        this.intersection = intersection;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Leaf getLeaf() {
        return leaf;
    }

    @Nullable
    public Difference getDifference() {
        return difference;
    }

    @Nullable
    public Nodes getUnion() {
        return union;
    }

    @Nullable
    public Nodes getIntersection() {
        return intersection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Node) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.leaf, that.leaf) &&
                Objects.equals(this.difference, that.difference) &&
                Objects.equals(this.union, that.union) &&
                Objects.equals(this.intersection, that.intersection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, leaf, difference, union, intersection);
    }

    @Override
    public String toString() {
        return "Node[" +
                "name=" + name + ", " +
                "leaf=" + leaf + ", " +
                "difference=" + difference + ", " +
                "union=" + union + ", " +
                "intersection=" + intersection + ']';
    }

}
