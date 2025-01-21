package io.quarkiverse.openfga.test;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.RelUser;

public class Unchecked {

    private static final Class<RelObject> OBJ_TYPE = RelObject.class;
    private static final Class<RelUser> USER_TYPE = RelUser.class;

    public static RelObject object(String type, String id) {
        try {
            var cons = OBJ_TYPE.getDeclaredConstructor(String.class, String.class);
            cons.setAccessible(true);
            return OBJ_TYPE.cast(cons.newInstance(type, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RelUser user(String type, String id) {
        try {
            return user(object(type, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RelUser user(String type, String id, @Nullable String relation) {
        try {
            return user(object(type, id), relation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RelUser user(RelObject object) {
        try {
            return user(object, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RelUser user(RelObject object, @Nullable String relation) {
        try {
            var cons = USER_TYPE.getDeclaredConstructor(RelObject.class, String.class);
            cons.setAccessible(true);
            return USER_TYPE.cast(cons.newInstance(object, relation));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
