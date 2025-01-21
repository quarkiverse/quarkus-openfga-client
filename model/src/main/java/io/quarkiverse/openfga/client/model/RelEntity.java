package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkiverse.openfga.client.model.json.RelEntityDeserializer;
import io.quarkiverse.openfga.client.model.json.RelEntitySerializer;

/**
 * Represents an entity (object or user) of a relationship.
 */
@JsonSerialize(using = RelEntitySerializer.class)
@JsonDeserialize(using = RelEntityDeserializer.class)
public interface RelEntity extends RelTyped {

    /**
     * @return the entity's type
     */
    @Override
    String getType();

    /**
     * @return the entity's id
     */
    String getId();

    /**
     * Converts this entity to a {@link RelObject} with the same type and id.
     *
     * @return the entity as an {@link RelObject}
     */
    default RelObject asObject() {
        return RelObject.of(getType(), getId());
    }

    /**
     * Converts this entity to a {@link RelUser} with the same type and id.
     *
     * <p>
     * If the entity is already a user, it is returned as is, preserving any existing
     * {@link RelUser#getRelation() relation}.
     *
     * <p>
     * If the entity is an object, the user's relation will be {@code null}.
     *
     * @return the entity as a {@link RelUser}
     */
    default RelUser asUser() {
        return RelUser.of(getType(), getId());
    }

}
