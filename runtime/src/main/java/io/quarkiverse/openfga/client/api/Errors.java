package io.quarkiverse.openfga.client.api;

import static io.vertx.mutiny.core.http.HttpHeaders.CONTENT_TYPE;

import io.quarkiverse.openfga.client.model.FGAException;
import io.vertx.mutiny.ext.web.client.predicate.ErrorConverter;

class Errors {

    static final ErrorConverter errorConverter = ErrorConverter.createFullBody(result -> {

        var response = result.response();

        if (response.headers().contains(CONTENT_TYPE, "application/json", true)) {
            try {
                return response.bodyAsJson(FGAException.class);
            } catch (Throwable t) {
                // Ignore
            }
        }

        return new FGAException(FGAException.Code.UNKNOWN_ERROR, null);
    });

}
