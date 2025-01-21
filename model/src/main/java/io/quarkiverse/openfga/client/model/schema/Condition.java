package io.quarkiverse.openfga.client.model.schema;

import java.util.*;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;
import io.quarkiverse.openfga.client.model.utils.Strings;

public final class Condition {

    public static final class Parameter {

        public static Parameter of(String typeName, List<String> genericTypes) {
            return builder().typeName(typeName).genericTypes(genericTypes).build();
        }

        public static Parameter of(String typeName) {
            return of(typeName, List.of());
        }

        public static class Builder {
            private String typeName;
            private List<String> genericTypes = new ArrayList<>();

            private Builder() {
            }

            public Builder typeName(String typeName) {
                this.typeName = typeName;
                return this;
            }

            public Builder genericTypes(@Nullable Collection<String> genericTypes) {
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

            public Builder addGenericTypes(@Nullable Collection<String> genericTypes) {
                if (genericTypes == null) {
                    return this;
                }
                if (this.genericTypes == null) {
                    this.genericTypes = new ArrayList<>();
                }
                this.genericTypes.addAll(genericTypes);
                return this;
            }

            public Builder addGenericType(String genericType) {
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

        private final String typeName;
        private final List<String> genericTypes = new ArrayList<>();

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        Parameter(@JsonProperty("type_name") String typeName,
                @JsonProperty("generic_types") Collection<String> genericTypes) {
            this.typeName = Preconditions.parameterNonBlank(typeName, "typeName");
            this.genericTypes.addAll(Preconditions.parameterNonNull(genericTypes, "genericTypes"));
        }

        @JsonProperty("type_name")
        public String getTypeName() {
            return typeName;
        }

        @JsonProperty("generic_types")
        public List<String> getGenericTypes() {
            return genericTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;
            Parameter parameter = (Parameter) o;
            return Objects.equals(typeName, parameter.typeName) && Objects.equals(genericTypes, parameter.genericTypes);
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

        public Builder addParameter(String name, String typeName) {
            return addParameter(name, Parameter.of(typeName));
        }

        public Builder addParameter(String name, String typeName, List<String> genericTypes) {
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
