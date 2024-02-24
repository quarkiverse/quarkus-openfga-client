package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class AuthorizationModel {
    private final String id;
    private final String schemaVersion;
    private final List<TypeDefinition> typeDefinitions;
    @Nullable
    private final Map<String, Condition> conditions;

    public AuthorizationModel(String id, @JsonProperty("schema_version") String schemaVersion,
            @JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions,
            @JsonProperty("conditions") @Nullable Map<String, Condition> conditions) {
        this.id = Preconditions.parameterNonNull(id, "id");
        this.schemaVersion = Preconditions.parameterNonNull(schemaVersion, "schemaVersion");
        this.typeDefinitions = Preconditions.parameterNonNull(typeDefinitions, "typeDefinitions");
        this.conditions = conditions;
    }

    public String getId() {
        return id;
    }

    @JsonProperty("schema_version")
    public String getSchemaVersion() {
        return schemaVersion;
    }

    @JsonProperty("type_definitions")
    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    @JsonProperty("conditions")
    @Nullable
    public Map<String, Condition> getConditions() {
        return conditions;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (AuthorizationModel) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.typeDefinitions, that.typeDefinitions) &&
                Objects.equals(this.conditions, that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeDefinitions, conditions);
    }

    @Override
    public String toString() {
        return "AuthorizationModel[" +
                "id=" + id + ", " +
                "typeDefinitions=" + typeDefinitions + ", " +
                "conditions=" + conditions + ']';
    }

}
