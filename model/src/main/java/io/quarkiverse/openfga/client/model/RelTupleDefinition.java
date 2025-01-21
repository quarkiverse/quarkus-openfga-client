package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * A tuple definition including key elements and a condition.
 */
@JsonDeserialize
public final class RelTupleDefinition implements RelTupleKeyed {

    public static final class Builder {

        @Nullable
        private RelObject object;
        @Nullable
        private String relation;
        @Nullable
        private RelUser user;
        @Nullable
        private RelCondition condition;

        private Builder() {
        }

        public Builder object(RelObject object) {
            this.object = object;
            return this;
        }

        public Builder relation(String relation) {
            this.relation = relation;
            return this;
        }

        public Builder user(RelUser user) {
            this.user = user;
            return this;
        }

        public Builder condition(@Nullable RelCondition condition) {
            this.condition = condition;
            return this;
        }

        public RelTupleDefinition build() {
            var object = Preconditions.parameterNonNull(this.object, "object");
            var relation = Preconditions.parameterNonNull(this.relation, "relation");
            var user = Preconditions.parameterNonNull(this.user, "user");
            return new RelTupleDefinition(object, relation, user, condition);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final RelObject object;
    private final String relation;
    private final RelUser user;
    @Nullable
    private final RelCondition condition;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelTupleDefinition(RelObject object, String relation, RelUser user,
            @Nullable RelCondition condition) {
        this.object = object;
        this.relation = relation;
        this.user = user;
        this.condition = condition;
    }

    @Override
    public RelObject getObject() {
        return object;
    }

    @Override
    public String getRelation() {
        return relation;
    }

    @Override
    public RelUser getUser() {
        return user;
    }

    @Nullable
    public RelCondition getCondition() {
        return condition;
    }

    @Override
    public RelTupleDefinition conditional() {
        return this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof RelTupleDefinition that))
            return false;
        return Objects.equals(this.object, that.object) &&
                Objects.equals(this.relation, that.relation) &&
                Objects.equals(this.user, that.user) &&
                Objects.equals(this.condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, relation, user, condition);
    }

    @Override
    public String toString() {
        return "ConditionalTupleKey[" +
                "object=" + object + ", " +
                "relation=" + relation + ", " +
                "user=" + user + ", " +
                "condition=" + condition + ']';
    }
}
