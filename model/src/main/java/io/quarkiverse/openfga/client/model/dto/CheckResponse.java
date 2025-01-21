package io.quarkiverse.openfga.client.model.dto;

import javax.annotation.Nullable;

public record CheckResponse(boolean allowed, @Nullable String resolution) {
}
