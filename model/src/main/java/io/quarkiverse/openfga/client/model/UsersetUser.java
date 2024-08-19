package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class UsersetUser {

    private final String type;

    private final String id;

    private final String relation;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    UsersetUser(String type, String id, String relation) {
        this.type = type;
        this.id = id;
        this.relation = relation;
    }

    public static UsersetUser of(String type, String id, String relation) {
        return new UsersetUser(type, id, relation);
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getRelation() {
        return relation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (UsersetUser) obj;
        return java.util.Objects.equals(this.type, that.type) &&
                java.util.Objects.equals(this.id, that.id) &&
                java.util.Objects.equals(this.relation, that.relation);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, id, relation);
    }

    @Override
    public String toString() {
        return "UsersetUser[" +
                "type=" + type + ", " +
                "id=" + id + ", " +
                "relation=" + relation + ']';
    }
}
