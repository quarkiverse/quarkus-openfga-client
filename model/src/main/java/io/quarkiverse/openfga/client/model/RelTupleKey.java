package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * A key that identifies a specific tuple (object, relation, and user), absent any conditions.
 */
@JsonDeserialize
public final class RelTupleKey implements RelTupleKeyed {

    public static final class Builder {

        @Nullable
        private RelEntity object;
        @Nullable
        private String relation;
        @Nullable
        private RelEntity user;

        private Builder() {
        }

        public Builder object(RelEntity object) {
            this.object = object;
            return this;
        }

        public Builder relation(String relation) {
            this.relation = relation;
            return this;
        }

        public Builder user(RelEntity user) {
            this.user = user;
            return this;
        }

        public RelTupleKey build() {
            var object = Preconditions.parameterNonNull(this.object, "object").asObject();
            var relation = Preconditions.parameterNonNull(this.relation, "relation");
            var user = Preconditions.parameterNonNull(this.user, "user").asUser();
            return new RelTupleKey(object, relation, user);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final RelObject object;

    private final String relation;

    private final RelUser user;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelTupleKey(RelObject object, String relation, RelUser user) {
        this.object = object;
        this.relation = relation;
        this.user = user;
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

    public RelTupleDefinition withCondition(@Nullable RelCondition condition) {
        return RelTupleDefinition.builder()
                .object(object)
                .relation(relation)
                .user(user)
                .condition(condition)
                .build();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof RelTupleKey that))
            return false;
        return Objects.equals(this.object, that.object) &&
                Objects.equals(this.relation, that.relation) &&
                Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, relation, user);
    }

    @Override
    public String toString() {
        return "TupleKey[" +
                "object=" + object + ", " +
                "relation=" + relation + ", " +
                "user=" + user + ']';
    }

}
