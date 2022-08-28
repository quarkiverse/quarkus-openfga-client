package io.quarkiverse.openfga.client.api;

import static io.vertx.mutiny.uritemplate.Variables.variables;

import javax.annotation.Nullable;

import io.vertx.mutiny.uritemplate.Variables;

public class Vars {

    private static final Variables EMPTY_VARIABLES = variables();

    public static Variables vars() {
        return EMPTY_VARIABLES;
    }

    public static <V> Variables vars(String key1, @Nullable V value1) {
        var vars = Variables.variables();
        if (value1 != null) {
            vars.set(key1, value1.toString());
        }
        return vars;
    }

    public static <V1, V2> Variables vars(String key1, @Nullable V1 value1, String key2, @Nullable V2 value2) {
        var vars = vars(key1, value1);
        if (value2 != null) {
            vars.set(key2, value2.toString());
        }
        return vars;
    }

    public static <V1, V2, V3> Variables vars(String key1, @Nullable V1 value1, String key2, @Nullable V2 value2,
            String key3, @Nullable V3 value3) {
        var vars = vars(key1, value1, key2, value2);
        if (value3 != null) {
            vars.set(key3, value3.toString());
        }
        return vars;
    }

    public static <V1, V2, V3, V4> Variables vars(String key1, @Nullable V1 value1, String key2, @Nullable V2 value2,
            String key3, @Nullable V3 value3, String key4, @Nullable V4 value4) {
        var vars = vars(key1, value1, key2, value2, key3, value3);
        if (value4 != null) {
            vars.set(key4, value4.toString());
        }
        return vars;
    }

    public static <V1, V2, V3, V4, V5> Variables vars(String key1, @Nullable V1 value1, String key2, @Nullable V2 value2,
            String key3, @Nullable V3 value3, String key4, @Nullable V4 value4, String key5, @Nullable V4 value5) {
        var vars = vars(key1, value1, key2, value2, key3, value3, key4, value4);
        if (value5 != null) {
            vars.set(key5, value5.toString());
        }
        return vars;
    }

}
