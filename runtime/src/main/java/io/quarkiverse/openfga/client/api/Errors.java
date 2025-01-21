package io.quarkiverse.openfga.client.api;

import io.quarkiverse.openfga.client.model.FGAInputValidationException;
import io.quarkiverse.openfga.client.model.FGAInternalException;
import io.quarkiverse.openfga.client.model.FGANotFoundException;
import io.quarkiverse.openfga.client.model.FGAUnknownException;
import io.vertx.mutiny.ext.web.client.HttpResponse;

class Errors {

    static Throwable convert(HttpResponse<?> response) {
        return switch (response.statusCode()) {
            case 400 -> convertToValidationException(response);
            case 404 -> convertToNotFoundException(response);
            case 500 -> convertToInternalException(response);
            default -> new FGAUnknownException();
        };
    }

    private static FGAInputValidationException convertToValidationException(HttpResponse<?> response) {
        return response.bodyAsJson(FGAInputValidationException.class);
    }

    private static FGANotFoundException convertToNotFoundException(HttpResponse<?> response) {
        return response.bodyAsJson(FGANotFoundException.class);
    }

    private static FGAInternalException convertToInternalException(HttpResponse<?> response) {
        return response.bodyAsJson(FGAInternalException.class);
    }
}
