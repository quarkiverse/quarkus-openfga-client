package io.quarkiverse.openfga.client.utils;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record Pagination(@Nullable Integer pageSize, @Nullable String continuationToken) {

    public static final Pagination DEFAULT = new Pagination(null, null);

    public Pagination {
        pageSize = Preconditions.parameterNullableRange(pageSize, 1, 100, 10, "pageSize");
    }

    public static Pagination limitedTo(@Nullable Integer pageSize) {
        return new Pagination(pageSize, null);
    }

    public static Pagination continuingFrom(@Nullable String continuationToken) {
        return new Pagination(null, continuationToken);
    }

    public Pagination andLimitedTo(@Nullable Integer pageSize) {
        return new Pagination(pageSize, continuationToken);
    }

    public Pagination andContinuingFrom(@Nullable String continuationToken) {
        return new Pagination(pageSize, continuationToken);
    }
}
