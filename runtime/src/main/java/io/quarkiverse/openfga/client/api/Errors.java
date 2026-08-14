package io.quarkiverse.openfga.client.api;

import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.utils.ModelMapper;
import io.vertx.mutiny.ext.web.client.HttpResponse;

class Errors {

    static Throwable convert(HttpResponse<?> response) {
        return convert(response.statusCode(), response.bodyAsString());
    }

    static Throwable convert(int statusCode, String body) {
        try {
            return switch (statusCode) {
                case 400 -> ModelMapper.mapper.readValue(body, FGAValidationException.class);
                case 401, 403 -> ModelMapper.mapper.readValue(body, FGAAuthException.class);
                case 404 -> ModelMapper.mapper.readValue(body, FGANotFoundException.class);
                case 422 -> ModelMapper.mapper.readValue(body, FGAUnprocessableContentException.class);
                case 409, 500 -> ModelMapper.mapper.readValue(body, FGAInternalException.class);
                default -> new FGAUnknownException();
            };
        } catch (Throwable ignored) {
            return new FGAUnknownException();
        }
    }
}
