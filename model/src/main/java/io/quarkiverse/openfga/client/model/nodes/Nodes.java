package io.quarkiverse.openfga.client.model.nodes;

import java.util.List;
import java.util.Objects;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Nodes {
    private final List<Node> nodes;

    public Nodes(List<Node> nodes) {
        this.nodes = Preconditions.parameterNonNull(nodes, "nodes");
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Nodes) obj;
        return Objects.equals(this.nodes, that.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }

    @Override
    public String toString() {
        return "Nodes[" +
                "nodes=" + nodes + ']';
    }

}
