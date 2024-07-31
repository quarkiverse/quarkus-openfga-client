package io.quarkiverse.openfga.client.api;

public enum ExpectedStatus {

    OK(200),
    CREATED(201),
    NO_CONTENT(204);

    public final int statusCode;

    ExpectedStatus(int statusCode) {
        this.statusCode = statusCode;
    }
}
