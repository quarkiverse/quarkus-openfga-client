package io.quarkiverse.openfga.client.model.nodes;

import java.util.Objects;

import javax.annotation.Nullable;

public final class Leaf {
    @Nullable
    private final Users users;
    @Nullable
    private final Computed computed;
    @Nullable
    private final TupleToUserset tupleToUserset;

    public Leaf(
            @Nullable Users users,
            @Nullable Computed computed,
            @Nullable TupleToUserset tupleToUserset) {
        this.users = users;
        this.computed = computed;
        this.tupleToUserset = tupleToUserset;
    }

    @Nullable
    public Users getUsers() {
        return users;
    }

    @Nullable
    public Computed getComputed() {
        return computed;
    }

    @Nullable
    public TupleToUserset getTupleToUserset() {
        return tupleToUserset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Leaf) obj;
        return Objects.equals(this.users, that.users) &&
                Objects.equals(this.computed, that.computed) &&
                Objects.equals(this.tupleToUserset, that.tupleToUserset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, computed, tupleToUserset);
    }

    @Override
    public String toString() {
        return "Leaf[" +
                "users=" + users + ", " +
                "computed=" + computed + ", " +
                "tupleToUserset=" + tupleToUserset + ']';
    }

}
