package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Condition {

    public static class Parameter {

        private final String typeName;
        private final List<String> genericTypes;

        public Parameter(@JsonProperty("type_name") String typeName, @JsonProperty("generic_types") List<String> genericTypes) {
            this.typeName = typeName;
            this.genericTypes = genericTypes;
        }

        @JsonProperty("type_name")
        public String getTypeName() {
            return typeName;
        }

        @JsonProperty("generic_types")
        public List<String> getGenericTypes() {
            return genericTypes;
        }
    }

    private final String name;
    private final String expression;
    private final Map<String, Parameter> parameters;

    public Condition(String name, String expression, Map<String, Parameter> parameters) {
        this.name = name;
        this.expression = expression;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Condition condition = (Condition) o;
        return Objects.equals(name, condition.name) && Objects.equals(expression, condition.expression)
                && Objects.equals(parameters, condition.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, expression, parameters);
    }

    @Override
    public String toString() {
        return "Condition{" +
                "name='" + name + '\'' +
                ", expression='" + expression + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
