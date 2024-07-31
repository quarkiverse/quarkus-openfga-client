package io.quarkiverse.openfga.client.api;

import io.quarkiverse.openfga.client.model.FGAInternalException;
import io.quarkiverse.openfga.client.model.FGAUnknownException;
import io.quarkiverse.openfga.client.model.FGAValidationException;
import io.vertx.mutiny.ext.web.client.HttpResponse;

class Errors {

    static Throwable convert(HttpResponse<?> response) {
        try {
            return response.bodyAsJson(FGAValidationException.class);
        } catch (Throwable ignored) {
            try {
                return response.bodyAsJson(FGAInternalException.class);
            } catch (Throwable ignored2) {
                return new FGAUnknownException();
            }
        }
    }
}
