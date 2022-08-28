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

    public static <V1, V2> Map<String, String> query(String key1, @Nullable V1 value1, String key2, @Nullable V2 value2,
            String key3,
            @Nullable V2 value3) {
        var map = query(key1, value1, key2, value2);
        if (value3 != null) {
            map.put(key3, value3.toString());
        }
        return map;
    }

}
