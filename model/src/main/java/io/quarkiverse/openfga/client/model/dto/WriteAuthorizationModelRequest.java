package io.quarkiverse.openfga.client.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.AuthorizationModelSchema;
import io.quarkiverse.openfga.client.model.Condition;
import io.quarkiverse.openfga.client.model.TypeDefinition;

public final class WriteAuthorizationModelRequest {

    @JsonProperty("type_definitions")
    private final List<TypeDefinition> typeDefinitions;

    @JsonProperty("schema_version")
    private final String schemaVersion;

    @JsonProperty("conditions")
    @Nullable
    private final Map<String, Condition> conditions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    WriteAuthorizationModelRequest(@JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions, String schemaVersion,
            @Nullable Map<String, Condition> conditions) {
        this.typeDefinitions = typeDefinitions;
        this.schemaVersion = schemaVersion;
        this.conditions = conditions;
    }

    public static WriteAuthorizationModelRequest of(List<TypeDefinition> typeDefinitions, String schemaVersion,
            @Nullable Map<String, Condition> conditions) {
        return new WriteAuthorizationModelRequest(typeDefinitions, schemaVersion, conditions);
    }

    public static WriteAuthorizationModelRequest of(AuthorizationModelSchema schema) {
        return new WriteAuthorizationModelRequest(schema.getTypeDefinitions(), schema.getSchemaVersion(),
                schema.getConditions());
    }

    public static final class Builder {
        private List<TypeDefinition> typeDefinitions;
        private String schemaVersion;
        private Map<String, Condition> conditions;

        Builder() {
        }

        public Builder typeDefinitions(List<TypeDefinition> typeDefinitions) {
            this.typeDefinitions = typeDefinitions;
            return this;
        }

        public Builder addTypeDefinitions(List<TypeDefinition> typeDefinitions) {
            if (this.typeDefinitions == null) {
                this.typeDefinitions = new ArrayList<>();
            }
            this.typeDefinitions.addAll(typeDefinitions);
            return this;
        }

        public Builder addTypeDefinition(TypeDefinition typeDefinition) {
            if (this.typeDefinitions == null) {
                this.typeDefinitions = new ArrayList<>();
            }
            this.typeDefinitions.add(typeDefinition);
            return this;
        }

        public Builder schemaVersion(String schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }

        public Builder conditions(@Nullable Map<String, Condition> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder addConditions(Map<String, Condition> conditions) {
            if (this.conditions == null) {
                this.conditions = conditions;
            } else {
                this.conditions.putAll(conditions);
            }
            return this;
        }

        public Builder addCondition(String key, Condition condition) {
            if (this.conditions == null) {
                this.conditions = Map.of(key, condition);
            } else {
                this.conditions.put(key, condition);
            }
            return this;
        }

        public WriteAuthorizationModelRequest build() {
            return new WriteAuthorizationModelRequest(typeDefinitions, schemaVersion, conditions);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("type_definitions")
    public List<TypeDefinition> getTypeDefinitions() {
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
