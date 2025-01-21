package io.quarkiverse.openfga.client.model.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Preconditions {

    public static Integer parameterNullableRange(
            @Nullable Integer value,
            Integer min,
            Integer max,
            Integer defaultValue,
            String name) {
        if (value != null && (value < min || value > max)) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
        return value != null ? value : defaultValue;
    }

    public static int parameterNonNullRange(@Nullable Integer value, int min, int max, @Nonnull String name) {
        return parameterNonNull(parameterNullableRange(value, min, max, null, name), name);
    }

    public static @Nonnull String parameterNonBlank(String value, @Nonnull String name) {
        var val = parameterNonNull(value, name);
        if (val.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return val;
    }

    public static <T> @Nonnull T parameterNonNull(@Nullable T value, @Nonnull String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " parameter must not be null");
        }
        return value;
    }

    public static <T> @Nonnull T parameterNonNull(@Nullable T value, @Nonnull String name, Function<String, String> message) {
        if (value == null) {
            throw new IllegalArgumentException(message.apply(name));
        }
        return Objects.requireNonNull(value, message.apply(name));
    }

    public static <T, U extends T> @Nonnull U parameterIsInstance(@Nullable T value, Class<U> type, @Nonnull String name) {
        return parameterIsInstance(value, type, name, (n, t) -> n + " parameter must be an instance of " + t);
    }

    public static <T, U extends T> @Nonnull U parameterIsInstance(@Nullable T value, Class<U> type, @Nonnull String name,
            BiFunction<String, String, String> message) {
        Objects.requireNonNull(value, name + " parameter must not be null");
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(message.apply(name, type.getName()));
        }
        return type.cast(value);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> @Nonnull T parameterNonNull(Optional<T> value, @Nonnull String name) {
        return Objects.requireNonNull(value.orElse(null), name + " parameter must not be null");
    }

    public static <T> Optional<T> parameterNonNullToOptional(@Nullable T value, @Nonnull String name) {
        return Optional.of(parameterNonNull(value, name));
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
