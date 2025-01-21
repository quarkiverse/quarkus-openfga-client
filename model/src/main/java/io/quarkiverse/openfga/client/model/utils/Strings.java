package io.quarkiverse.openfga.client.model.utils;

import javax.annotation.Nullable;

public final class Strings {

    public static String emptyToNull(@Nullable String string) {
        return string == null || string.isEmpty() ? null : string;
    }

    private Strings() {
    }
}
