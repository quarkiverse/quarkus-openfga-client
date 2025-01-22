package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Schema.Condition;
import io.quarkiverse.openfga.client.model.Schema.TypeDefinition;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class AuthorizationModel {

    public AuthorizationModel of(String id, String schemaVersion, List<TypeDefinition> typeDefinitions,
            @Nullable Map<String, Condition> conditions) {
        return new AuthorizationModel(id, schemaVersion, typeDefinitions, conditions);
    }

    private final String id;
    private final String schemaVersion;
    private final List<TypeDefinition> typeDefinitions;
    @Nullable
    private final Map<String, Condition> conditions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    AuthorizationModel(String id, @JsonProperty("schema_version") String schemaVersion,
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
        if (!(obj instanceof AuthorizationModel that))
            return false;
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
