package io.quarkiverse.openfga.client.model.schema;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class UsersetUser {

    public static UsersetUser of(String type, String id, String relation) {
        return new UsersetUser(type, id, relation);
    }

    private final String type;

    private final String id;

    private final String relation;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    UsersetUser(String type, String id, String relation) {
        this.type = type;
        this.id = id;
        this.relation = relation;
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
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UsersetUser that))
            return false;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.relation, that.relation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, relation);
    }

    @Override
    public String toString() {
        return "UsersetUser[" +
                "type=" + type + ", " +
                "id=" + id + ", " +
                "relation=" + relation + ']';
    }
}
