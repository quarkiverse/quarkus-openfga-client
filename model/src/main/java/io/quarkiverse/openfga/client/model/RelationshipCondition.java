package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

public class RelationshipCondition {

    private final String name;

    private final Object context;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelationshipCondition(String name, @Nullable Object context) {
        this.name = name;
        this.context = context;
    }

    public static RelationshipCondition of(String name, @Nullable Object context) {
        return new RelationshipCondition(name, context);
    }

    public static RelationshipCondition of(String name) {
        return of(name, null);
    }

    public String getName() {
        return name;
    }

    /**
     * Additional context/data to persist along with the condition.
     *
     * The keys must match the parameters defined by the condition, and the value types must
     * match the parameter type definitions.
     */
    @Nullable
    public Object getContext() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RelationshipCondition that = (RelationshipCondition) o;
        return Objects.equals(name, that.name) && Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, context);
    }

    @Override
    public String toString() {
        return "RelationshipCondition{" +
                "name='" + name + '\'' +
                ", context=" + context +
                '}';
    }
}
