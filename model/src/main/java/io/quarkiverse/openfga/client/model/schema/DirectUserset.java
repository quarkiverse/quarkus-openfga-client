package io.quarkiverse.openfga.client.model.schema;

import java.util.HashMap;

public final class DirectUserset extends HashMap<String, Object> {

    public static DirectUserset of() {
        return new DirectUserset();
    }

    public static DirectUserset of(String k1, Object v1) {
        var direct = new DirectUserset();
        direct.put(k1, v1);
        return direct;
    }

    public static DirectUserset of(String k1, Object v1, String k2, Object v2) {
        var direct = of(k1, v1);
        direct.put(k2, v2);
        return direct;
    }

    public static DirectUserset of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        var direct = of(k1, v1, k2, v2);
        direct.put(k3, v3);
        return direct;
    }

    public static DirectUserset of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
            String k4, Object v4) {
        var direct = of(k1, v1, k2, v2, k3, v3);
        direct.put(k4, v4);
        return direct;
    }

    public static DirectUserset of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
            String k4, Object v4, String k5, Object v5) {
        var direct = of(k1, v1, k2, v2, k3, v3, k4, v4);
        direct.put(k5, v5);
        return direct;
    }

}
