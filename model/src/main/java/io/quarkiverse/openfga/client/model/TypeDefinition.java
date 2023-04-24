package io.quarkiverse.openfga.client.model;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TypeDefinition {
    private final String type;
    @Nullable
    private final Map<String, Userset> relations;
    @Nullable
    private final Metadata metadata;

    @JsonCreator
    public TypeDefinition(String type, @Nullable Map<String, Userset> relations, @Nullable Metadata metadata) {
        this.type = Preconditions.parameterNonNull(type, "type");
        this.relations = relations;
        this.metadata = metadata;
    }

    public TypeDefinition(String type, @Nullable Map<String, Userset> relations) {
        this(type, relations, null);
    }

    public TypeDefinition(String type) {
        this(type, null, null);
    }

    public String getType() {
        return type;
    }

    @Nullable
    public Map<String, Userset> getRelations() {
        return relations;
    }

    @Nullable
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TypeDefinition))
            return false;
        TypeDefinition that = (TypeDefinition) o;
        return Objects.equals(type, that.type) && Objects.equals(relations, that.relations)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relations, metadata);
    }

    @Override
    public String toString() {
        return "TypeDefinition{" +
                "type='" + type + '\'' +
                ", relations=" + relations +
                ", metadata=" + metadata +
                '}';
    }
}
