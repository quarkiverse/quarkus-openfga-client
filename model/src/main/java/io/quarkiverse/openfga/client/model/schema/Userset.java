package io.quarkiverse.openfga.client.model.schema;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Userset {

    public static Userset direct(DirectUserset directUserset) {
        return new Userset(directUserset, null, null, null, null, null);
    }

    public static Userset direct() {
        return direct(DirectUserset.of());
    }

    public static Userset direct(String k1, Object v1) {
        return direct(DirectUserset.of(k1, v1));
    }

    public static Userset direct(String k1, Object v1, String k2, Object v2) {
        return direct(DirectUserset.of(k1, v1, k2, v2));
    }

    public static Userset direct(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return direct(DirectUserset.of(k1, v1, k2, v2, k3, v3));
    }

    public static Userset direct(String k1, Object v1, String k2, Object v2, String k3, Object v3,
            String k4, Object v4) {
        return direct(DirectUserset.of(k1, v1, k2, v2, k3, v3, k4, v4));
    }

    public static Userset direct(String k1, Object v1, String k2, Object v2, String k3, Object v3,
            String k4, Object v4, String k5, Object v5) {
        return direct(DirectUserset.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
    }

    public static Userset computed(ObjectRelation computedUserset) {
        return new Userset(null, computedUserset, null, null, null, null);
    }

    public static Userset computed(String object, String relation) {
        return computed(new ObjectRelation(object, relation));
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

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Userset(@Nullable @JsonProperty("this") DirectUserset directUserset, @Nullable ObjectRelation computedUserset,
            @Nullable V1.TupleToUserset tupleToUserset, @Nullable Usersets union, @Nullable Usersets intersection,
            @Nullable V1.Difference difference) {
        this.directUserset = directUserset;
        this.computedUserset = computedUserset;
        this.tupleToUserset = tupleToUserset;
        this.union = union;
        this.intersection = intersection;
        this.difference = difference;
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
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Userset userset))
            return false;
        return Objects.equals(directUserset, userset.directUserset) &&
                Objects.equals(computedUserset, userset.computedUserset) &&
                Objects.equals(tupleToUserset, userset.tupleToUserset) &&
                Objects.equals(union, userset.union) &&
                Objects.equals(intersection, userset.intersection) &&
                Objects.equals(difference, userset.difference);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(directUserset);
        result = 31 * result + Objects.hashCode(computedUserset);
        result = 31 * result + Objects.hashCode(tupleToUserset);
        result = 31 * result + Objects.hashCode(union);
        result = 31 * result + Objects.hashCode(intersection);
        result = 31 * result + Objects.hashCode(difference);
        return result;
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
