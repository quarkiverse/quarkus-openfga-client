package io.quarkiverse.openfga.client.model.utils;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Preconditions {

    public static Integer parameterNullableRange(@Nullable Integer value, int min, int max, int defaultValue,
            @Nonnull String name) {
        if (value != null && (value < min || value > max)) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
        return value != null ? value : defaultValue;
    }

    public static <T> @Nonnull T parameterNonNull(@Nonnull T value, @Nonnull String name) {
        return Objects.requireNonNull(value, name + " parameter must not be null");
    }

    public static void oneOfNonNull(String message, Object... values) {
        for (Object value : values) {
            if (value != null) {
                return;
            }
        }
        throw new NullPointerException(message);
    }

}
