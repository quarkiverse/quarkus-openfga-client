package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TupleKey {

    private final String object;

    private final String relation;

    private final String user;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    TupleKey(String object, String relation, String user) {
        this.object = Preconditions.parameterNonNull(object, "object");
        this.relation = Preconditions.parameterNonNull(relation, "relation");
        this.user = Preconditions.parameterNonNull(user, "user");
    }

    public static TupleKey of(String object, String relation, String user) {
        return new TupleKey(object, relation, user);
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

    public ConditionalTupleKey condition(@Nullable RelationshipCondition condition) {
        return new ConditionalTupleKey(object, relation, user, condition);
    }

    public ConditionalTupleKey nullCondition() {
        return new ConditionalTupleKey(object, relation, user, null);
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
