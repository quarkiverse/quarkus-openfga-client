package io.quarkiverse.openfga.client.model.schema;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;
import io.quarkiverse.openfga.client.model.utils.Strings;

public final class RelationReference {

    public static RelationReference of(String type) {
        return of(type, null, null, null);
    }

    public static RelationReference of(String type, String relation) {
        return of(type, relation, null, null);
    }

    public static RelationReference of(String type, @Nullable String relation, @Nullable Object wildcard,
            @Nullable String condition) {
        return new RelationReference(type, relation, wildcard, condition);
    }

    public static final class Builder {
        private String type;

        @Nullable
        private String relation;

        @Nullable
        private Object wildcard;

        @Nullable
        private String condition;

        private Builder() {
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder relation(@Nullable String relation) {
            this.relation = relation;
            return this;
        }

        public Builder wildcard(@Nullable Object wildcard) {
            this.wildcard = wildcard;
            return this;
        }

        public Builder condition(@Nullable String condition) {
            this.condition = condition;
            return this;
        }

        public RelationReference build() {
            return new RelationReference(type, relation, wildcard, condition);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final String type;

    @Nullable
    private final String relation;

    @Nullable
    private final Object wildcard;

    @Nullable
    private final String condition;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelationReference(String type, @Nullable String relation, @Nullable Object wildcard, @Nullable String condition) {
        this.type = Preconditions.parameterNonBlank(type, "type");
        this.relation = Strings.emptyToNull(relation);
        this.wildcard = wildcard;
        this.condition = Strings.emptyToNull(condition);
    }

    public String getType() {
        return type;
    }

    @Nullable
    public String getRelation() {
        return relation;
    }

    @Nullable
    public Object getWildcard() {
        return wildcard;
    }

    @Nullable
    public String getCondition() {
        return condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RelationReference that))
            return false;
        return Objects.equals(type, that.type) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(wildcard, that.wildcard) &&
                Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relation, wildcard, condition);
    }

    @Override
    public String toString() {
        return "RelationReference[" +
                "type=" + type + ", " +
                "relation=" + relation + ", " +
                "wildcard=" + wildcard + ", " +
                "condition=" + condition + ']';
    }
}
