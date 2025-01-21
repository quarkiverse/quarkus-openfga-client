package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * Represents an object of a relationship.
 */
@JsonDeserialize
public final class RelObject implements RelEntity {

    /**
     * Create a {@link RelObject} from a string representation of the format {@code type:id}.
     *
     * @param value The string representation of the object.
     * @return A {@link RelObject} with the given type and id.
     */
    public static RelObject valueOf(String value) {
        var parts = value.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid typed object: " + value);
        }
        return RelObject.of(parts[0], parts[1]);
    }

    /**
     * Create a {@link RelObject} with the given type and id.
     *
     * @param type The type of the object.
     * @param id The id of the object.
     * @return A {@link RelObject} with the given type and id.
     */
    public static RelObject of(String type, String id) {
        return new RelObject(
                Preconditions.parameterNonBlank(type, "type"),
                Preconditions.parameterNonBlank(id, "id"));
    }

    /**
     * Create a {@link RelObject} with only a type, and no id.
     *
     * @apiNote This is only allowed for specific cases (e.g., read requests) that allow missing (aka empty) ids.
     * @param type The type of the object.
     * @return A {@link RelObject} with the given type and an empty id.
     */
    public static RelObject typeOnly(String type) {
        return new RelObject(type, "");
    }

    private final String type;
    private final String id;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelObject(@JsonProperty String type, @JsonProperty String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public RelObject withType(String type) {
        return RelObject.of(type, id);
    }

    public RelObject withId(String id) {
        return RelObject.of(type, id);
    }

    @Override
    public RelObject asObject() {
        return this;
    }

    public RelTupleDefinition define(String relation, RelUser user) {
        return define().relation(relation).user(user).build();
    }

    public RelTupleDefinition.Builder define() {
        return RelTupleDefinition.builder().object(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof RelObject that))
            return false;
        return Objects.equals(this.type, that.getType()) &&
                Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    @Override
    public String toString() {
        return type + ":" + id;
    }
}
