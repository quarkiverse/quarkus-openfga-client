package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class TypedWildcard {

    private final String type;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    TypedWildcard(String type) {
        this.type = type;
    }

    public static TypedWildcard of(String type) {
        return new TypedWildcard(type);
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (TypedWildcard) obj;
        return java.util.Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(type);
    }

    @Override
    public String toString() {
        return "TypedWildcard[" +
                "type=" + type + ']';
    }
}
