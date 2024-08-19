package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class UserTypeFilter {

    private final String type;

    @Nullable
    private final String relation;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    UserTypeFilter(String type, @Nullable String relation) {
        this.type = Preconditions.parameterNonNull(type, "type");
        this.relation = relation;
    }

    public static UserTypeFilter of(String type) {
        return of(type, null);
    }

    public static UserTypeFilter of(String type, @Nullable String relation) {
        return new UserTypeFilter(type, relation);
    }

    public static final class Builder {
        private String type;
        private String relation;

        public Builder() {
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder relation(@Nullable String relation) {
            this.relation = relation;
            return this;
        }

        public UserTypeFilter build() {
            return new UserTypeFilter(type, relation);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getType() {
        return type;
    }

    @Nullable
    public String getRelation() {
        return relation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (UserTypeFilter) o;
        return type.equals(that.type) &&
                Objects.equals(relation, that.relation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relation);
    }

    @Override
    public String toString() {
        return "UserTypeFilter[" +
                "type='" + type + "', " +
                "relation='" + relation + ']';
    }

}
