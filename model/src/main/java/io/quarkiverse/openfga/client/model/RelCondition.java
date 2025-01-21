package io.quarkiverse.openfga.client.model;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * A condition that must be met for a relationship to be considered valid.
 * <br>
 * The condition is defined by the model and can be used to enforce constraints on the relationship.
 * <br>
 * The condition must be defined in the model before it can be used in a relationship.
 */
public class RelCondition {

    public static RelCondition of(String name, @Nullable Map<String, Object> context) {
        return new RelCondition(name, context);
    }

    public static RelCondition of(String name) {
        return of(name, null);
    }

    private final String name;
    @Nullable
    private final Map<String, Object> context;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelCondition(String name, @Nullable Map<String, Object> context) {
        this.name = Preconditions.parameterNonBlank(name, "name");
        this.context = context;
    }

    /**
     * The name of the condition that must match a condition defined in the model.
     */
    public String getName() {
        return name;
    }

    /**
     * Additional context/data to persist along with the condition.
     * <br>
     * The keys must match the parameters defined by the condition, and the value types must
     * match the parameter type definitions.
     */
    @Nullable
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof RelCondition that))
            return false;
        return Objects.equals(name, that.name) &&
                Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, context);
    }

    @Override
    public String toString() {
        return "RelCondition[" +
                "name=" + name + ", " +
                "context=" + context + ']';
    }
}
