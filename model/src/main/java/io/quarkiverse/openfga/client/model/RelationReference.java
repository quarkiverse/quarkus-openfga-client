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

    @JsonCreator
    public RelationReference(String type, @Nullable String relation, @Nullable Object wildcard) {
        this.type = type;
        this.relation = relation;
        this.wildcard = wildcard;
    }

    public RelationReference(String type) {
        this(type, null, null);
    }

    public RelationReference(String type, String relation) {
        this(type, relation, null);
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RelationReference))
            return false;
        RelationReference that = (RelationReference) o;
        return Objects.equals(type, that.type) && Objects.equals(relation, that.relation)
                && Objects.equals(wildcard, that.wildcard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relation, wildcard);
    }

    @Override
    public String toString() {
        return "RelationReference{" +
                "type='" + type + '\'' +
                ", relation='" + relation + '\'' +
                ", wildcard=" + wildcard +
                '}';
    }
}
