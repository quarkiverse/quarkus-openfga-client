package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkiverse.openfga.client.model.json.RelTupleKeyedDeserializer;

/**
 * Any object that provides the elements a relationship key in the OpenFGA system, consisting of an
 * object, a relation and a user.
 */
@JsonDeserialize(using = RelTupleKeyedDeserializer.class)
public sealed interface RelTupleKeyed extends RelPartialTupleKeyed permits RelTupleKey, RelTupleDefinition {

    /**
     * Returns the object associated with this key.
     *
     * @return the object associated with this key
     */
    @Override
    RelObject getObject();

    /**
     * Returns the relation associated with this key.
     *
     * @return the relation associated with this key
     */
    @Override
    String getRelation();

    /**
     * Returns the user associated with this key.
     *
     * @return the user associated with this key
     */
    RelUser getUser();

    /**
     * Returns a {@link RelTupleKey} with the same object, relation and user.
     *
     * @return a {@link RelTupleKey} with the same object, relation and user
     */
    default RelTupleKey key() {
        return RelTupleKey.builder().object(getObject()).relation(getRelation()).user(getUser()).build();
    }

    /**
     * Returns a {@link RelPartialTupleKey} with the same object and relation.
     *
     * @return a {@link RelPartialTupleKey} with the same object and relation
     */
    default RelPartialTupleKey relationship() {
        return RelPartialTupleKey.builder().object(getObject()).relation(getRelation()).build();
    }

    /**
     * Returns a {@link RelTupleDefinition} with the same object, relation and user.
     * If the key is already a {@link RelTupleDefinition}, it is returned as is with the same condition.
     *
     * @return a {@link RelTupleDefinition} with the same object, relation and user
     */
    default RelTupleDefinition conditional() {
        return RelTupleDefinition.builder().object(getObject()).relation(getRelation()).user(getUser()).build();
    }

    /**
     * Returns a {@link RelTupleDefinition} with the same object, relation and user, but with the given condition.
     * If the key is already a {@link RelTupleDefinition}, it is returned as is with the same object, relation and
     * user, but with the given condition.
     *
     * @param condition the condition to set
     * @return a {@link RelTupleDefinition} with the same object, relation and user, but with the given condition
     */
    default RelTupleDefinition withCondition(RelCondition condition) {
        return RelTupleDefinition.builder()
                .object(getObject())
                .relation(getRelation())
                .user(getUser())
                .condition(condition)
                .build();
    }
}
