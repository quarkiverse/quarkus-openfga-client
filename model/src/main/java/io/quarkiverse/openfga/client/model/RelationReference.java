package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

public class RelationReference {
    String type;
    @Nullable
    String relation;
    @Nullable
    Object wildcard;
    @Nullable
    String condition;

    @JsonCreator
    public RelationReference(String type, @Nullable String relation, @Nullable Object wildcard, @Nullable String condition) {
        this.type = type;
        this.relation = relation;
        this.wildcard = wildcard;
        this.condition = condition;
    }

    public RelationReference(String type) {
        this(type, null, null, null);
    }

    public RelationReference(String type, String relation) {
        this(type, relation, null, null);
    }

    public String getType() {
        return type;
    }

    @Nullable
    public String getRelation() {
        return relation;
    }

    @Nullable
    public Object getWildcard() {
        return wildcard;
    }

    @Nullable
    public String getCondition() {
        return condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RelationReference))
            return false;
        RelationReference that = (RelationReference) o;
        return Objects.equals(type, that.type) && Objects.equals(relation, that.relation)
                && Objects.equals(wildcard, that.wildcard) && Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relation, wildcard, condition);
    }

    @Override
    public String toString() {
        return "RelationReference{" +
                "type='" + type + '\'' +
                ", relation='" + relation + '\'' +
                ", wildcard=" + wildcard + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }
}
