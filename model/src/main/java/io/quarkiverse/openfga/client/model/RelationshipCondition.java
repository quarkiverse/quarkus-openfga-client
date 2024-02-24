package io.quarkiverse.openfga.client.model;

import java.util.Objects;

public class RelationshipCondition {

    private final String name;
    private final Object context;

    public RelationshipCondition(String name, Object context) {
        this.name = name;
        this.context = context;
    }

    public String getName() {
        return name;
    }

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
