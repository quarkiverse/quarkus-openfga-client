package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TupleKey {
    private final String object;
    private final String relation;
    private final String user;
    private final RelationshipCondition condition;

    public TupleKey(String object, String relation, String user, @Nullable RelationshipCondition condition) {
        this.object = Preconditions.parameterNonNull(object, "object");
        this.relation = Preconditions.parameterNonNull(relation, "relation");
        this.user = Preconditions.parameterNonNull(user, "user");
        this.condition = condition;
    }

    public static TupleKey of(String object, String relation, String user) {
        return new TupleKey(object, relation, user, null);
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (TupleKey) obj;
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
        return "TupleKey[" +
                "object=" + object + ", " +
                "relation=" + relation + ", " +
                "user=" + user + ", " +
                "condition=" + condition + ']';
    }

}
