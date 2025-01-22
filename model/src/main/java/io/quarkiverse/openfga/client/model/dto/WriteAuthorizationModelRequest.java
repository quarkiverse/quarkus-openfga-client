package io.quarkiverse.openfga.client.model.dto;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Schema.Condition;
import io.quarkiverse.openfga.client.model.Schema.TypeDefinition;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class WriteAuthorizationModelRequest {

    public static final class Builder {

        private @Nullable Collection<TypeDefinition> typeDefinitions;
        private @Nullable String schemaVersion;
        private @Nullable Map<String, Condition> conditions;

        private Builder() {
        }

        public Builder typeDefinitions(Collection<TypeDefinition> typeDefinitions) {
            this.typeDefinitions = typeDefinitions;
            return this;
        }

        public Builder schemaVersion(@Nullable String schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }

        public Builder conditions(@Nullable Map<String, Condition> conditions) {
            this.conditions = conditions;
            return this;
        }

        public WriteAuthorizationModelRequest build() {
            return new WriteAuthorizationModelRequest(
                    Preconditions.parameterNonNull(typeDefinitions, "typeDefinitions"),
                    Preconditions.parameterNonBlank(schemaVersion, "schemaVersion"),
                    conditions);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Collection<TypeDefinition> typeDefinitions;
    private final String schemaVersion;
    @Nullable
    private final Map<String, Condition> conditions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    WriteAuthorizationModelRequest(@JsonProperty("type_definitions") Collection<TypeDefinition> typeDefinitions,
            String schemaVersion, @Nullable Map<String, Condition> conditions) {
        this.typeDefinitions = typeDefinitions;
        this.schemaVersion = schemaVersion;
        this.conditions = conditions;
    }

    @JsonProperty("type_definitions")
    public Collection<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    @JsonProperty("schema_version")
    public String getSchemaVersion() {
        return schemaVersion;
    }

    @Nullable
    public Map<String, Condition> getConditions() {
        return conditions;
    }
}
