package io.quarkiverse.openfga.client.model.nodes;

import java.util.Objects;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Difference {
    private final Node base;
    private final Node subtract;

    public Difference(Node base, Node subtract) {
        this.base = Preconditions.parameterNonNull(base, "base");
        this.subtract = Preconditions.parameterNonNull(subtract, "subtract");
    }

    public Node getBase() {
        return base;
    }

    public Node getSubtract() {
        return subtract;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Difference) obj;
        return Objects.equals(this.base, that.base) &&
                Objects.equals(this.subtract, that.subtract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, subtract);
    }

    @Override
    public String toString() {
        return "Difference[" +
                "base=" + base + ", " +
                "subtract=" + subtract + ']';
    }

}
