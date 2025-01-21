package io.quarkiverse.openfga.client.model;

import java.io.*;
import java.util.*;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.schema.Condition;
import io.quarkiverse.openfga.client.model.utils.ModelMapper;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class AuthorizationModelSchema {

    public static final String DEFAULT_SCHEMA_VERSION = "1.1";

    public static final class Builder {

        private String schemaVersion = DEFAULT_SCHEMA_VERSION;
        private List<TypeDefinition> typeDefinitions = new ArrayList<>();
        @Nullable
        private Map<String, Condition> conditions;

        private Builder() {
        }

        public Builder schemaVersion(@Nullable String schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }

        public Builder typeDefinitions(@Nullable Collection<TypeDefinition> typeDefinitions) {
            if (typeDefinitions == null) {
                return this;
            }
            this.typeDefinitions = new ArrayList<>(typeDefinitions);
            return this;
        }

        public Builder addTypeDefinitions(TypeDefinition... typeDefinitions) {
            return typeDefinitions(List.of(typeDefinitions));
        }

        public Builder addTypeDefinition(TypeDefinition typeDefinition) {
            this.typeDefinitions.add(typeDefinition);
            return this;
        }

        public Builder conditions(@Nullable Map<String, Condition> conditions) {
            if (conditions == null) {
                this.conditions = null;
                return this;
            }
            this.conditions = new HashMap<>(conditions);
            return this;
        }

        public Builder addCondition(String name, Condition condition) {
            if (this.conditions == null) {
                this.conditions = new HashMap<>();
            }
            this.conditions.put(name, condition);
            return this;
        }

        public AuthorizationModelSchema build() {
            return new AuthorizationModelSchema(schemaVersion, typeDefinitions, conditions);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AuthorizationModelSchema parse(InputStream stream) throws IOException {
        return ModelMapper.mapper.readValue(stream, AuthorizationModelSchema.class);
    }

    public static AuthorizationModelSchema parse(Reader reader) throws IOException {
        return ModelMapper.mapper.readValue(reader, AuthorizationModelSchema.class);
    }

    public static AuthorizationModelSchema parse(byte[] bytes) throws IOException {
        return ModelMapper.mapper.readValue(bytes, AuthorizationModelSchema.class);
    }

    public static AuthorizationModelSchema parse(String json) throws IOException {
        return ModelMapper.mapper.readValue(json, AuthorizationModelSchema.class);
    }

    private final String schemaVersion;
    private final List<TypeDefinition> typeDefinitions;
    @Nullable
    private final Map<String, Condition> conditions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    AuthorizationModelSchema(@JsonProperty("schema_version") String schemaVersion,
            @JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions,
            @Nullable Map<String, Condition> conditions) {
        this.schemaVersion = schemaVersion;
        this.typeDefinitions = Preconditions.parameterNonNull(typeDefinitions, "typeDefinitions");
        this.conditions = conditions;
    }

    @JsonProperty("schema_version")
    public String getSchemaVersion() {
        return schemaVersion;
    }

    @JsonProperty("type_definitions")
    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    @Nullable
    public Map<String, Condition> getConditions() {
        return conditions;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof AuthorizationModelSchema that))
            return false;
        return schemaVersion.equals(that.schemaVersion) &&
                typeDefinitions.equals(that.typeDefinitions) &&
                Objects.equals(conditions, that.conditions);
    }

    @Override
    public int hashCode() {
        int result = schemaVersion.hashCode();
        result = 31 * result + typeDefinitions.hashCode();
        result = 31 * result + Objects.hashCode(conditions);
        return result;
    }

    @Override
    public String toString() {
        return "AuthorizationModelSchema[" +
                "schemaVersion=" + schemaVersion + ", " +
                "typeDefinitions=" + typeDefinitions + ", " +
                "conditions=" + conditions + ']';
    }
}
