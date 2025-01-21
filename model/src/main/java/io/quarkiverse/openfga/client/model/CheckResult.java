package io.quarkiverse.openfga.client.model;

import javax.annotation.Nullable;

/**
 * The result of a check.
 * <br>
 * The result is defined by whether the check is allowed, and an optional error.
 * <br>
 * The allowed flag is used to indicate whether the check is allowed.
 * <br>
 * The error is used to indicate an error that occurred during the check.
 */
public record CheckResult(boolean allowed, @Nullable CheckError error) {
}
