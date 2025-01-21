package io.quarkiverse.openfga.client.model.schema;

import java.util.*;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.*;

import io.quarkiverse.openfga.client.model.utils.Preconditions;
import io.quarkiverse.openfga.client.model.utils.Strings;

public final class Condition {

    public static final class Parameter {

        public enum TypeName {
            UNSPECIFIED("TYPE_NAME_UNSPECIFIED"),
            ANY("TYPE_NAME_ANY"),
            BOOL("TYPE_NAME_BOOL"),
            STRING("TYPE_NAME_STRING"),
            INT("TYPE_NAME_INT"),
            UINT("TYPE_NAME_UINT"),
            DOUBLE("TYPE_NAME_DOUBLE"),
            DURATION("TYPE_NAME_DURATION"),
            TIMESTAMP("TYPE_NAME_TIMESTAMP"),
            MAP("TYPE_NAME_MAP"),
            LIST("TYPE_NAME_LIST"),
            IPADDRESS("TYPE_NAME_IPADDRESS"),
            UNKNOWN("");

            private final String value;

            TypeName(String value) {
                this.value = value;
            }

            @JsonValue
            public String getValue() {
                return value;
            }

            @JsonCreator
            public static TypeName fromValue(String value) {
                for (var e : TypeName.values()) {
                    if (e.value.equals(value)) {
                        return e;
                    }
                }
                return UNKNOWN;
            }
        }

        public static class GenericType extends HashMap<String, Object> {

            private GenericType(Map<String, Object> map) {
                super(map);
            }
        }

        public static Parameter of(TypeName typeName, Collection<GenericType> genericTypes) {
            return builder().typeName(typeName).genericTypes(genericTypes).build();
        }

        public static Parameter of(TypeName typeName) {
            return of(typeName, List.of());
        }

        public static class Builder {
            private TypeName typeName;
            private Collection<GenericType> genericTypes = new ArrayList<>();

            private Builder() {
            }

            public Builder typeName(TypeName typeName) {
                this.typeName = typeName;
                return this;
            }

            public Builder genericTypes(@Nullable Collection<GenericType> genericTypes) {
                if (genericTypes == null) {
                    this.genericTypes.clear();
                    return this;
                }
                if (this.genericTypes == null) {
                    this.genericTypes = new ArrayList<>();
                    return this;
                }
                this.genericTypes.addAll(genericTypes);
                return this;
            }

            public Builder addGenericTypes(@Nullable Collection<GenericType> genericTypes) {
                if (genericTypes == null) {
                    return this;
                }
                if (this.genericTypes == null) {
                    this.genericTypes = new ArrayList<>();
                }
                this.genericTypes.addAll(genericTypes);
                return this;
            }

            public Builder addGenericType(GenericType genericType) {
                if (this.genericTypes == null) {
                    this.genericTypes = new ArrayList<>();
                }
                this.genericTypes.add(genericType);
                return this;
            }

            public Parameter build() {
                return new Parameter(typeName, genericTypes);
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        private final TypeName typeName;
        private final Collection<GenericType> genericTypes = new ArrayList<>();

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        Parameter(@JsonProperty("type_name") TypeName typeName,
                @JsonProperty("generic_types") Collection<GenericType> genericTypes) {
            this.typeName = Preconditions.parameterNonNull(typeName, "typeName");
            this.genericTypes.addAll(Preconditions.parameterNonNull(genericTypes, "genericTypes"));
        }

        @JsonProperty("type_name")
        public TypeName getTypeName() {
            return typeName;
        }

        @JsonProperty("generic_types")
        public Collection<GenericType> getGenericTypes() {
            return genericTypes;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Parameter that))
                return false;
            return Objects.equals(typeName, that.typeName) && Objects.equals(genericTypes, that.genericTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeName, genericTypes);
        }

        @Override
        public String toString() {
            return "Parameter[" +
                    "typeName=" + typeName + ", " +
                    "genericTypes=" + genericTypes + ']';
        }
    }

    public static final class Metadata {

        public static Metadata of(@Nullable String sourceInfo, @Nullable String module, @Nullable Map<String, Object> extra) {
            return new Metadata(sourceInfo, module, extra);
        }

        @Nullable
        private final String sourceInfo;
        @Nullable
        private final String module;
        private final Map<String, Object> extra;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        Metadata(@JsonProperty("source_info") @Nullable String sourceInfo,
                @JsonProperty("module") @Nullable String module,
                @JsonAnySetter @Nullable Map<String, Object> extra) {
            this.sourceInfo = Strings.emptyToNull(sourceInfo);
            this.module = Strings.emptyToNull(module);
            this.extra = Optional.ofNullable(extra).map(HashMap::new).orElseGet(HashMap::new);
        }

        @JsonProperty("source_info")
        @Nullable
        public String getSourceInfo() {
            return sourceInfo;
        }

        @Nullable
        public String getModule() {
            return module;
        }

        @JsonAnyGetter
        public Map<String, Object> getExtra() {
            return extra;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof Metadata that))
                return false;
            return Objects.equals(this.sourceInfo, that.sourceInfo) &&
                    Objects.equals(this.module, that.module) &&
                    Objects.equals(this.extra, that.extra);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceInfo, module, extra);
        }

        @Override
        public String toString() {
            return "Metadata[" +
                    "sourceInfo=" + sourceInfo + ", " +
                    "module=" + module + "," +
                    "extra=" + extra + ']';
        }
    }

    public static final class Builder {

        private String name;
        private String expression;
        private Map<String, Parameter> parameters = new HashMap<>();
        @Nullable
        private Metadata metadata;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        public Builder parameters(Map<String, Parameter> parameters) {
            this.parameters = new HashMap<>(parameters);
            return this;
        }

        public Builder addParameters(Map<String, Parameter> parameters) {
            if (this.parameters == null) {
                this.parameters = new HashMap<>();
            }
            this.parameters.putAll(parameters);
            return this;
        }

        public Builder addParameter(String name, Parameter parameter) {
            if (this.parameters == null) {
                this.parameters = new HashMap<>();
            }
            this.parameters.put(name, parameter);
            return this;
        }

        public Builder addParameter(String name, Parameter.TypeName typeName) {
            return addParameter(name, Parameter.of(typeName));
        }

        public Builder addParameter(String name, Parameter.TypeName typeName, Collection<Parameter.GenericType> genericTypes) {
            return addParameter(name, Parameter.of(typeName, genericTypes));
        }

        public Builder metadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Condition build() {
            return new Condition(name, expression, parameters, metadata);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final String name;
    private final String expression;
    @Nullable
    private final Map<String, Parameter> parameters;
    @Nullable
    private final Metadata metadata;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Condition(String name, String expression, @Nullable Map<String, Parameter> parameters,
            @Nullable Metadata metadata) {
        this.name = Preconditions.parameterNonBlank(name, "name");
        this.expression = Preconditions.parameterNonBlank(expression, "expression");
        this.parameters = Optional.ofNullable(parameters).map(HashMap::new).orElseGet(HashMap::new);
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    @Nullable
    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    @Nullable
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Condition that))
            return false;
        return Objects.equals(name, that.name) &&
                Objects.equals(expression, that.expression) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, expression, parameters, metadata);
    }

    @Override
    public String toString() {
        return "Condition[" +
                "name=" + name + ", " +
                "expression=" + expression + ", " +
                "parameters=" + parameters + ", " +
                "metadata=" + metadata + ']';
    }
}
