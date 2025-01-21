package io.quarkiverse.openfga.client.model.schema;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.RelObjectType;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class User {

    public static User object(RelObject object) {
        return new User(object, null, null);
    }

    public static User object(String type, String id) {
        return object(RelObject.of(type, id));
    }

    public static User userset(UsersetUser userset) {
        return new User(null, userset, null);
    }

    public static User userset(String type, String id, String relation) {
        return userset(UsersetUser.of(type, id, relation));
    }

    public static User wildcard(RelObjectType wildcard) {
        return new User(null, null, wildcard);
    }

    public static User wildcard(String type) {
        return wildcard(RelObjectType.of(type));
    }

    @Nullable
    private final RelObject object;

    @Nullable
    private final UsersetUser userset;

    @Nullable
    private final RelObjectType wildcard;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    User(@Nullable RelObject object, @Nullable UsersetUser userset, @Nullable RelObjectType wildcard) {
        Preconditions.oneOfNonNull("User must have exactly one of object, userset, wildcard", object, userset, wildcard);
        this.object = object;
        this.userset = userset;
        this.wildcard = wildcard;
    }

    @Nullable
    public Object getObject() {
        return object;
    }

    @Nullable
    public UsersetUser getUserset() {
        return userset;
    }

    @Nullable
    public RelObjectType getWildcard() {
        return wildcard;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user))
            return false;
        return Objects.equals(object, user.object) &&
                Objects.equals(userset, user.userset) &&
                Objects.equals(wildcard, user.wildcard);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(object);
        result = 31 * result + Objects.hashCode(userset);
        result = 31 * result + Objects.hashCode(wildcard);
        return result;
    }

    @Override
    public String toString() {
        return "User[" +
                "object=" + object + ", " +
                "userset=" + userset + ", " +
                "wildcard=" + wildcard + ']';
    }

}
