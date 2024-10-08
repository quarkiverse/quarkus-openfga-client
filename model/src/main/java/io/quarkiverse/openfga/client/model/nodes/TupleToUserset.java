package io.quarkiverse.openfga.client.model.nodes;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TupleToUserset {

    private final String tupleset;

    private final List<Computed> computed;

    public TupleToUserset(String tupleset, List<Computed> computed) {
        this.tupleset = Preconditions.parameterNonNull(tupleset, "tupleset");
        this.computed = Preconditions.parameterNonNull(computed, "computed");
    }

    public String getTupleset() {
        return tupleset;
    }

    public List<Computed> getComputed() {
        return computed;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (TupleToUserset) obj;
        return Objects.equals(this.tupleset, that.tupleset) &&
                Objects.equals(this.computed, that.computed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleset, computed);
    }

    @Override
    public String toString() {
        return "TupleToUserset[" +
                "tupleset=" + tupleset + ", " +
                "computed=" + computed + ']';
    }

}
