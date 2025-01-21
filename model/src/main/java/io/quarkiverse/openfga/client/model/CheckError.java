package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.smallrye.common.constraint.Nullable;

/**
 * An error that occurred during the check.
 * <br>
 * The error is defined by an input error, an internal error, and an optional message.
 * <br>
 * The input error is used to indicate an error in the input data.
 * <br>
 * The internal error is used to indicate an error in the internal processing.
 * <br>
 * The message is used to provide additional information about the error.
 */
public record CheckError(@JsonProperty("input_error") @Nullable ErrorCode inputError,
        @JsonProperty("internal_error") @Nullable InternalErrorCode internalError, @Nullable String message) {
}
