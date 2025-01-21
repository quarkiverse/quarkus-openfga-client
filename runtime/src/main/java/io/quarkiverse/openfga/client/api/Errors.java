package io.quarkiverse.openfga.client.api;

import io.quarkiverse.openfga.client.model.*;
import io.vertx.mutiny.ext.web.client.HttpResponse;

class Errors {

    static Throwable convert(HttpResponse<?> response) {
        return switch (response.statusCode()) {
            case 400 -> convertToValidationException(response);
            case 401, 403 -> convertToAuthException(response);
            case 404 -> convertToNotFoundException(response);
            case 422 -> convertToUnprocessableContentException(response);
            case 409, 500 -> convertToInternalException(response);
            default -> new FGAUnknownException();
        };
    }

    private static FGAAuthException convertToAuthException(HttpResponse<?> response) {
        return response.bodyAsJson(FGAAuthException.class);
    }

    private static FGAValidationException convertToValidationException(HttpResponse<?> response) {
        return response.bodyAsJson(FGAValidationException.class);
    }

    private static FGANotFoundException convertToNotFoundException(HttpResponse<?> response) {
        return response.bodyAsJson(FGANotFoundException.class);
    }

    private static FGAInternalException convertToInternalException(HttpResponse<?> response) {
        return response.bodyAsJson(FGAInternalException.class);
    }

    private static FGAUnprocessableContentException convertToUnprocessableContentException(HttpResponse<?> response) {
        return response.bodyAsJson(FGAUnprocessableContentException.class);
    }
}
