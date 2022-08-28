package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TypeDefinitions {
    @JsonProperty("type_definitions")
    private final List<TypeDefinition> typeDefinitions;

    public TypeDefinitions(@JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions) {
        this.typeDefinitions = Preconditions.parameterNonNull(typeDefinitions, "typeDefinitions");
    }

    @JsonProperty("type_definitions")
    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (TypeDefinitions) obj;
        return Objects.equals(this.typeDefinitions, that.typeDefinitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeDefinitions);
    }

    @Override
    public String toString() {
        return "TypeDefinitions[" +
                "typeDefinitions=" + typeDefinitions + ']';
    }

}
