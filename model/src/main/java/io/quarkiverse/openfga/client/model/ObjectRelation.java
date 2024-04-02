package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ObjectRelation {

    @Nullable
    private final String object;
    private final String relation;

    @JsonCreator
    public ObjectRelation(@Nullable String object, String relation) {
        this.object = object;
        this.relation = Preconditions.parameterNonNull(relation, "relation");
    }

    @Nullable
    public String getObject() {
        return object;
    }

    public String getRelation() {
        return relation;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
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
