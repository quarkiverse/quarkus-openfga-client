package io.quarkiverse.openfga.client.model;

/**
 * Any object that provides the partial key elements, object and relation, of a {@link RelTupleKeyed} key.
 */
public interface RelPartialTupleKeyed {

    /**
     * Returns the object associated with this key.
     *
     * @return the object associated with this key
     */
    RelObject getObject();

    /**
     * Returns the relation associated with this key.
     *
     * @return the relation associated with this key
     */
    String getRelation();

}
