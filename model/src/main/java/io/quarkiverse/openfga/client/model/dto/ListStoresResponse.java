package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ListStoresResponse {

    private final List<Store> stores;

    @JsonProperty("continuation_token")
    private final String continuationToken;

    @JsonCreator(mode = PROPERTIES)
    public ListStoresResponse(List<Store> stores, @JsonProperty("continuation_token") String continuationToken) {
        this.stores = Preconditions.parameterNonNull(stores, "stores");
        this.continuationToken = continuationToken;
    }

    public List<Store> getStores() {
        return stores;
    }

    @JsonProperty("continuation_token")
    public String getContinuationToken() {
        return continuationToken;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ListStoresResponse) obj;
        return Objects.equals(this.stores, that.stores) &&
                Objects.equals(this.continuationToken, that.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stores, continuationToken);
    }

    @Override
    public String toString() {
        return "ListStoresResponse[" +
                "stores=" + stores + ", " +
                "continuationToken=" + continuationToken + ']';
    }

}
