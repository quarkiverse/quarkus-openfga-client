package io.quarkiverse.openfga.client.api;

import io.quarkiverse.openfga.client.model.FGAException;
import io.vertx.mutiny.ext.web.client.predicate.ErrorConverter;

class Errors {

    static final ErrorConverter errorConverter = ErrorConverter.createFullBody(result -> {

        var response = result.response();

        try {
            return response.bodyAsJson(FGAException.class);
        } catch (Throwable ignored) {
            return new FGAException(FGAException.Code.UNKNOWN_ERROR, "An unknown error occurred");
        }
    });

}
