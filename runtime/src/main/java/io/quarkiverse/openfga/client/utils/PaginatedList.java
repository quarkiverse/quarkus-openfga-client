package io.quarkiverse.openfga.client.utils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.utils.Preconditions;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public final class PaginatedList<T> {

    private final Collection<T> items;
    @Nullable
    private final String token;

    public PaginatedList(Collection<T> items, @Nullable String token) {
        this.items = List.copyOf(Preconditions.parameterNonNull(items, "items"));
        this.token = token;
    }

    public Boolean isNotLastPage() {
        return !items.isEmpty() && token != null && !token.isEmpty();
    }

    public static <T> Uni<List<T>> collectAllPages(@Nullable Integer pageSize,
            Function<Pagination, Uni<PaginatedList<T>>> listGenerator) {
        return Multi.createBy()
                .repeating()
                .uni(AtomicReference<String>::new,
                        lastToken -> listGenerator.apply(Pagination.continuingFrom(lastToken.get()).andLimitedTo(pageSize))
                                .onItem().invoke(list -> lastToken.set(list.getToken())))
                .whilst(PaginatedList::isNotLastPage)
                .onItem().transformToIterable(PaginatedList::getItems)
                .collect().asList();
    }

    public Collection<T> getItems() {
        return items;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (PaginatedList<?>) obj;
        return Objects.equals(this.items, that.items) &&
                Objects.equals(this.token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, token);
    }

    @Override
    public String toString() {
        return "PaginatedList[" +
                "items=" + items + ", " +
                "token=" + token + ']';
    }

}
