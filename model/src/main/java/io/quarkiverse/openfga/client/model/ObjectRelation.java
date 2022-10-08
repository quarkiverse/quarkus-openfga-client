package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ObjectRelation {
    private final String object;
    private final String relation;

    @JsonCreator
    public ObjectRelation(String object, String relation) {
        this.object = Preconditions.parameterNonNull(object, "object");
        this.relation = Preconditions.parameterNonNull(relation, "relation");
    }

    public String getObject() {
        return object;
    }

    public String getRelation() {
        return relation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ObjectRelation) obj;
        return Objects.equals(this.object, that.object) &&
                Objects.equals(this.relation, that.relation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, relation);
    }

    @Override
    public String toString() {
        return "ObjectRelation[" +
                "object=" + object + ", " +
                "relation=" + relation + ']';
    }

}
