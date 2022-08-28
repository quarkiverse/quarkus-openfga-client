package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.nodes.V1;

public final class Userset {
    @Nullable
    @JsonProperty("this")
    private final DirectUserset directUserset;
    @Nullable
    private final ObjectRelation computedUserset;
    @Nullable
    private final V1.TupleToUserset tupleToUserset;
    @Nullable
    private final Usersets union;
    @Nullable
    private final Usersets intersection;
    @Nullable
    private final V1.Difference difference;

    public Userset(
            @Nullable @JsonProperty("this") DirectUserset directUserset,
            @Nullable ObjectRelation computedUserset,
            @Nullable V1.TupleToUserset tupleToUserset,
            @Nullable Usersets union,
            @Nullable Usersets intersection,
            @Nullable V1.Difference difference) {
        this.directUserset = directUserset;
        this.computedUserset = computedUserset;
        this.tupleToUserset = tupleToUserset;
        this.union = union;
        this.intersection = intersection;
        this.difference = difference;
    }

    public static Userset direct() {
        return new Userset(new DirectUserset(), null, null, null, null, null);
    }

    public static Userset computed(ObjectRelation computedUserset) {
        return new Userset(null, computedUserset, null, null, null, null);
    }

    public static Userset tupleTo(V1.TupleToUserset tupleToUserset) {
        return new Userset(null, null, tupleToUserset, null, null, null);
    }

    public static Userset union(Usersets union) {
        return new Userset(null, null, null, union, null, null);
    }

    public static Userset intersection(Usersets intersection) {
        return new Userset(null, null, null, null, intersection, null);
    }

    public static Userset difference(V1.Difference difference) {
        return new Userset(null, null, null, null, null, difference);
    }

    @Nullable
    @JsonProperty("this")
    public DirectUserset getDirectUserset() {
        return directUserset;
    }

    @Nullable
    public ObjectRelation getComputedUserset() {
        return computedUserset;
    }

    @Nullable
    public V1.TupleToUserset getTupleToUserset() {
        return tupleToUserset;
    }

    @Nullable
    public Usersets getUnion() {
        return union;
    }

    @Nullable
    public Usersets getIntersection() {
        return intersection;
    }

    @Nullable
    public V1.Difference getDifference() {
        return difference;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Userset) obj;
        return Objects.equals(this.directUserset, that.directUserset) &&
                Objects.equals(this.computedUserset, that.computedUserset) &&
                Objects.equals(this.tupleToUserset, that.tupleToUserset) &&
                Objects.equals(this.union, that.union) &&
                Objects.equals(this.intersection, that.intersection) &&
                Objects.equals(this.difference, that.difference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directUserset, computedUserset, tupleToUserset, union, intersection, difference);
    }

    @Override
    public String toString() {
        return "Userset[" +
                "directUserset=" + directUserset + ", " +
                "computedUserset=" + computedUserset + ", " +
                "tupleToUserset=" + tupleToUserset + ", " +
                "union=" + union + ", " +
                "intersection=" + intersection + ", " +
                "difference=" + difference + ']';
    }

}
