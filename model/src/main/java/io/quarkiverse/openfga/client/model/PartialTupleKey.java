package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

public final class PartialTupleKey {
    @Nullable
    private final String object;
    @Nullable
    private final String relation;
    @Nullable
    private final String user;

    public PartialTupleKey(
            @Nullable String object,
            @Nullable String relation,
            @Nullable String user) {
        this.object = object;
        this.relation = relation;
        this.user = user;
    }

    public static PartialTupleKey of(@Nullable String object, @Nullable String relation, @Nullable String user) {
        return new PartialTupleKey(object, relation, user);
    }

    @Nullable
    public String getObject() {
        return object;
    }

    @Nullable
    public String getRelation() {
        return relation;
    }

    @Nullable
    public String getUser() {
        return user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (PartialTupleKey) obj;
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
        return "PartialTupleKey[" +
                "object=" + object + ", " +
                "relation=" + relation + ", " +
                "user=" + user + ']';
    }

}
