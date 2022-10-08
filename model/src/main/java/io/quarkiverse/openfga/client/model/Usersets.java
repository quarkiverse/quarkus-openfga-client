package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Usersets {
    private final List<Userset> child;

    @JsonCreator
    public Usersets(List<Userset> child) {
        this.child = Preconditions.parameterNonNull(child, "child");
    }

    public List<Userset> getChild() {
        return child;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Usersets) obj;
        return Objects.equals(this.child, that.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(child);
    }

    @Override
    public String toString() {
        return "Usersets[" +
                "child=" + child + ']';
    }

}
