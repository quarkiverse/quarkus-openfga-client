package io.quarkiverse.openfga.client.model.schema;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Usersets {

    public static Usersets of(List<Userset> child) {
        return new Usersets(child);
    }

    private final List<Userset> child;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Usersets(List<Userset> child) {
        this.child = Preconditions.parameterNonNull(child, "child");
    }

    public List<Userset> getChild() {
        return child;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Usersets that))
            return false;
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
