package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * Represents a user in a relationship.
 */
@JsonDeserialize
public final class RelUser implements RelEntity {

    public static RelUser valueOf(String value) {
        var parts = value.split("#", 2);
        var object = RelObject.valueOf(parts[0]);
        String relation = null;
        if (parts.length == 2) {
            relation = parts[1];
        }
        return RelUser.of(object, relation);
    }

    public static RelUser of(String type, String id) {
        return of(type, id, null);
    }

    public static RelUser of(RelObject object) {
        return of(object.getType(), object.getId());
    }

    public static RelUser of(String type, String id, @Nullable String relation) {
        return new RelUser(RelObject.of(type, id), relation);
    }

    public static RelUser of(RelObject object, @Nullable String relation) {
        return new RelUser(
                Preconditions.parameterNonNull(object, "object"),
                relation);
    }

    private final RelObject object;
    @Nullable
    private final String relation;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelUser(@JsonProperty RelObject object, @JsonProperty @Nullable String relation) {
        this.object = object;
        this.relation = relation;
    }

    @Override
    public String getType() {
        return object.getType();
    }

    public String getId() {
        return object.getId();
    }

    @Nullable
    public String getRelation() {
        return relation;
    }

    @Override
    public RelObject asObject() {
        return object;
    }

    @Override
    public RelUser asUser() {
        return this;
    }

    public RelTupleDefinition define(String relation, RelObject object) {
        return define().relation(relation).object(object).build();
    }

    public RelTupleDefinition.Builder define() {
        return RelTupleDefinition.builder().user(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof RelUser that))
            return false;
        return Objects.equals(this.object, that.object) &&
                Objects.equals(this.relation, that.relation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, relation);
    }

    @Override
    public String toString() {
        if (relation == null) {
            return object.toString();
        }
        return object + "#" + relation;
    }
}
