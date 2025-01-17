package io.quarkiverse.openfga.client.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class Queries {

    public static <V> Map<String, String> query(String key1, @Nullable V value1) {
        var map = new HashMap<String, String>();
        if (value1 != null) {
            map.put(key1, value1.toString());
        }
        return map;
    }

    public static <V1, V2> Map<String, String> query(String key1, @Nullable V1 value1, String key2, @Nullable V2 value2) {
        var map = query(key1, value1);
        if (value2 != null) {
            map.put(key2, value2.toString());
        }
        return map;
    }

    public static <V1, V2, V3> Map<String, String> query(String key1, @Nullable V1 value1, String key2, @Nullable V2 value2,
            String key3,
            @Nullable V3 value3) {
        var map = query(key1, value1, key2, value2);
        if (value3 != null) {
            map.put(key3, value3.toString());
        }
        return map;
    }

    public static <V1, V2, V3, V4> Map<String, String> query(String key1, @Nullable V1 value1, String key2, @Nullable V2 value2,
            String key3, @Nullable V3 value3, String key4, @Nullable V4 value4) {
        var map = query(key1, value1, key2, value2, key3, value3);
        if (value4 != null) {
            map.put(key4, value4.toString());
        }
        return map;
    }

}
