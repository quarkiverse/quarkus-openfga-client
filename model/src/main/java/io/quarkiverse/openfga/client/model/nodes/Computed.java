package io.quarkiverse.openfga.client.model.nodes;

import java.util.Objects;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class Computed {
    private final String userset;

    public Computed(String userset) {
        this.userset = Preconditions.parameterNonNull(userset, "userset");
    }

    public String getUserset() {
        return userset;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Computed) obj;
        return Objects.equals(this.userset, that.userset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userset);
    }

    @Override
    public String toString() {
        return "Computed[" +
                "userset=" + userset + ']';
    }

}
