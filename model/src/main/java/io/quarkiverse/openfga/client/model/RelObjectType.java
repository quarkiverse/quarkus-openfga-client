package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * Represents a type of entity.
 */
public final class RelObjectType implements RelTyped {

    public static final String WILDCARD_TYPE_NAME = "*";

    public static final RelObjectType ANY = new RelObjectType(WILDCARD_TYPE_NAME);

    public static RelObjectType of(String type) {
        if (type.equals(WILDCARD_TYPE_NAME)) {
            return ANY;
        }
        return new RelObjectType(type);
    }

    @JsonCreator
    public static RelObjectType valueOf(String value) {
        var parts = value.split(":", 2);
        var type = parts[0];
        if (parts.length == 2 && !parts[1].equals(WILDCARD_TYPE_NAME) && !parts[1].isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid object type: %s. Id must be empty or '%s'".formatted(value, WILDCARD_TYPE_NAME));
        }
        return RelObjectType.of(type);
    }

    private final String type;

    private RelObjectType(String type) {
        this.type = Preconditions.parameterNonBlank(type, "type");
    }

    @Override
    public String getType() {
        return type;
    }

    public RelObject toObject(String id) {
        return RelObject.of(type, id);
    }

    public RelUser toUser(String id) {
        return RelUser.of(type, id);
    }

    public RelUser toUser(String id, @Nullable String relation) {
        return RelUser.of(type, id, relation);
    }

    @Override
    public RelObjectType toType() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (RelObjectType) obj;
        return Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @JsonValue
    @Override
    public String toString() {
        return type + ":";
    }
}
