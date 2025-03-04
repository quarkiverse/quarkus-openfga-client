package io.quarkiverse.openfga.client.model.utils;

import java.util.Map;

import javax.annotation.Nullable;

public class Maps {

    public static <K, V> @Nullable Map<K, V> emptyToNull(@Nullable Map<K, V> map) {
        return map == null || map.isEmpty() ? null : map;
    }

}
