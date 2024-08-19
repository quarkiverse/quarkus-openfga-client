package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class User {

    @Nullable
    private final AnyObject object;

    @Nullable
    private final UsersetUser userset;

    @Nullable
    private final TypedWildcard wildcard;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    User(@Nullable AnyObject object, @Nullable UsersetUser userset, @Nullable TypedWildcard wildcard) {
        Preconditions.oneOfNonNull("User must have exactly one of object, userset, wildcard", object, userset, wildcard);
        this.object = object;
        this.userset = userset;
        this.wildcard = wildcard;
    }

    public static User object(AnyObject object) {
        return new User(object, null, null);
    }

    public static User object(String type, String id) {
        return object(AnyObject.of(type, id));
    }

    public static User userset(UsersetUser userset) {
        return new User(null, userset, null);
    }

    public static User userset(String type, String id, String relation) {
        return userset(UsersetUser.of(type, id, relation));
    }

    public static User wildcard(TypedWildcard wildcard) {
        return new User(null, null, wildcard);
    }

    public static User wildcard(String type) {
        return wildcard(TypedWildcard.of(type));
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
    public TypedWildcard getWildcard() {
        return wildcard;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (User) obj;
        return Objects.equals(this.object, that.object) &&
                Objects.equals(this.userset, that.userset) &&
                Objects.equals(this.wildcard, that.wildcard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, userset, wildcard);
    }

    @Override
    public String toString() {
        return "User[" +
                "object=" + object + ", " +
                "userset=" + userset + ", " +
                "wildcard=" + wildcard + ']';
    }

}
