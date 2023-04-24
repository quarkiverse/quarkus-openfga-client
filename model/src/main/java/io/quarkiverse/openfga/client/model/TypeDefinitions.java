package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TypeDefinitions {
    @JsonProperty("schema_version")
    @Nullable
    private final String schemaVersion;

    @JsonProperty("type_definitions")
    private final List<TypeDefinition> typeDefinitions;

    @JsonCreator
    public TypeDefinitions(@JsonProperty("schema_version") @Nullable String schemaVersion,
            @JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions) {
        this.schemaVersion = schemaVersion;
        this.typeDefinitions = Preconditions.parameterNonNull(typeDefinitions, "typeDefinitions");
    }

    public TypeDefinitions(@JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions) {
        this.schemaVersion = null;
        this.typeDefinitions = Preconditions.parameterNonNull(typeDefinitions, "typeDefinitions");
    }

    @JsonProperty("schema_version")
    @Nullable
    public String getSchemaVersion() {
        return schemaVersion;
    }

    @JsonProperty("type_definitions")
    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TypeDefinitions))
            return false;
        TypeDefinitions that = (TypeDefinitions) o;
        return Objects.equals(schemaVersion, that.schemaVersion) && Objects.equals(typeDefinitions, that.typeDefinitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaVersion, typeDefinitions);
    }

    @Override
    public String toString() {
        return "TypeDefinitions{" +
                "schemaVersion='" + schemaVersion + '\'' +
                ", typeDefinitions=" + typeDefinitions +
                '}';
    }
}
