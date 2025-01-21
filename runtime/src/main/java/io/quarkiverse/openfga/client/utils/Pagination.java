package io.quarkiverse.openfga.client.utils;

import java.util.Optional;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public record Pagination(Integer pageSize, Optional<String> continuationToken) {

    public static final Pagination DEFAULT = new Pagination(10, Optional.empty());
    public static final Pagination MAX = new Pagination(100, Optional.empty());

    public Pagination {
        pageSize = Preconditions.parameterNonNullRange(pageSize, 1, 100, "pageSize");
    }

    public static Pagination limitedTo(@Nullable Integer pageSize) {
        return Pagination.DEFAULT.andLimitedTo(pageSize);
    }

    public static Pagination continuingFrom(@Nullable String continuationToken) {
        return Pagination.DEFAULT.andContinuingFrom(continuationToken);
    }

    public Pagination andLimitedTo(@Nullable Integer pageSize) {
        return new Pagination(Optional.ofNullable(pageSize).orElse(DEFAULT.pageSize), continuationToken);
    }

    public Pagination andContinuingFrom(@Nullable String continuationToken) {
        return new Pagination(pageSize, Optional.ofNullable(continuationToken));
    }
}
