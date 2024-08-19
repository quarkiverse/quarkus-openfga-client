package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ConditionalTupleKey {

    private final String object;

    private final String relation;

    private final String user;

    @Nullable
    private final RelationshipCondition condition;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ConditionalTupleKey(String object, String relation, String user, @Nullable RelationshipCondition condition) {
        this.object = Preconditions.parameterNonNull(object, "object");
        this.relation = Preconditions.parameterNonNull(relation, "relation");
        this.user = Preconditions.parameterNonNull(user, "user");
        this.condition = condition;
    }

    public static ConditionalTupleKey of(String object, String relation, String user,
            @Nullable RelationshipCondition condition) {
        return new ConditionalTupleKey(object, relation, user, condition);
    }

    public static ConditionalTupleKey of(String object, String relation, String user) {
        return new ConditionalTupleKey(object, relation, user, null);
    }

    public String getObject() {
        return object;
    }

    public String getRelation() {
        return relation;
    }

    public String getUser() {
        return user;
    }

    @Nullable
    public RelationshipCondition getCondition() {
        return condition;
    }

    public TupleKey withoutCondition() {
        return TupleKey.of(object, relation, user);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ConditionalTupleKey) obj;
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
