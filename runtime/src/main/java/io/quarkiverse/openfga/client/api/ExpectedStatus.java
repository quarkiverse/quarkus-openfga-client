package io.quarkiverse.openfga.client.api;

import static io.quarkiverse.openfga.client.api.Errors.errorConverter;

import io.vertx.mutiny.ext.web.client.predicate.ResponsePredicate;

public enum ExpectedStatus {

    OK(ResponsePredicate.create(ResponsePredicate.SC_OK, errorConverter)),
    CREATED(ResponsePredicate.create(ResponsePredicate.SC_CREATED, errorConverter)),
    NO_CONTENT(ResponsePredicate.create(ResponsePredicate.SC_NO_CONTENT, errorConverter));

    public final ResponsePredicate responsePredicate;

    ExpectedStatus(ResponsePredicate responsePredicate) {
        this.responsePredicate = responsePredicate;
    }

}
