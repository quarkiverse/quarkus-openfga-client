package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class AuthorizationModelSchema {

    @JsonProperty("schema_version")
    private final String schemaVersion;

    @JsonProperty("type_definitions")
    private final List<TypeDefinition> typeDefinitions;

    @Nullable
    private final Map<String, Condition> conditions;

    @JsonCreator
    AuthorizationModelSchema(@JsonProperty("schema_version") String schemaVersion,
            @JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions,
            @Nullable Map<String, Condition> conditions) {
        this.schemaVersion = schemaVersion;
        this.typeDefinitions = Preconditions.parameterNonNull(typeDefinitions, "typeDefinitions");
        this.conditions = conditions;
    }

    public static AuthorizationModelSchema of(String schemaVersion, List<TypeDefinition> typeDefinitions,
            @Nullable Map<String, Condition> conditions) {
        return new AuthorizationModelSchema(schemaVersion, typeDefinitions, conditions);
    }

    public static AuthorizationModelSchema of(@JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions,
            @Nullable Map<String, Condition> conditions) {
        return of(Defaults.SCHEMA_VERSION, typeDefinitions, conditions);
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
        var that = (AuthorizationModelSchema) obj;
        return Objects.equals(this.schemaVersion, that.schemaVersion) &&
                Objects.equals(this.typeDefinitions, that.typeDefinitions) &&
                Objects.equals(this.conditions, that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaVersion, typeDefinitions, conditions);
    }

    @Override
    public String toString() {
        return "AuthorizationModelSchema[" +
                "schemaVersion=" + schemaVersion + ", " +
                "typeDefinitions=" + typeDefinitions + ", " +
                "conditions=" + conditions + ']';
    }
}
