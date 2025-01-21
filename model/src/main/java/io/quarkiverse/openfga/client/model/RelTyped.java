package io.quarkiverse.openfga.client.model;

/**
 * Any object that has an explicit type.
 */
public interface RelTyped {

    /**
     * @return the type of the value
     */
    String getType();

    /**
     * Converts the value to a {@link RelObjectType}.
     *
     * @return the value's type as an {@link RelObjectType}
     */
    default RelObjectType toType() {
        return RelObjectType.of(getType());
    }

}
