package io.quarkiverse.openfga.client.model;

import javax.annotation.Nullable;

public abstract class FGAException extends Exception {
    public FGAException(@Nullable String message) {
        super(message);
    }
}
