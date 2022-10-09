package io.quarkiverse.openfga.client.model;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TypeDefinition {
    private final String type;
    private final Map<String, Userset> relations;
    private final Object metadata;

    @JsonCreator
    public TypeDefinition(String type, Map<String, Userset> relations, @Nullable Object metadata) {
        this.type = Preconditions.parameterNonNull(type, "type");
        this.relations = Preconditions.parameterNonNull(relations, "relations");
        this.metadata = metadata;
    }

    public TypeDefinition(String type, Map<String, Userset> relations) {
        this(type, relations, null);
    }

    public String getType() {
        return type;
    }

    public Map<String, Userset> getRelations() {
        return relations;
    }

    @Nullable
    public Object getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (TypeDefinition) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.relations, that.relations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relations);
    }

    @Override
    public String toString() {
        return "TypeDefinition[" +
                "type=" + type + ", " +
                "relations=" + relations + ']';
    }

}
