package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Tuple;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ReadResponse {

    private final List<Tuple> tuples;

    @JsonProperty("continuation_token")
    private final String continuationToken;

    @JsonCreator(mode = PROPERTIES)
    public ReadResponse(List<Tuple> tuples, @JsonProperty("continuation_token") String continuationToken) {
        this.tuples = Preconditions.parameterNonNull(tuples, "tuples");
        this.continuationToken = Preconditions.parameterNonNull(continuationToken, "continuationToken");
    }

    public List<Tuple> getTuples() {
        return tuples;
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
        var that = (ReadResponse) obj;
        return Objects.equals(this.tuples, that.tuples) &&
                Objects.equals(this.continuationToken, that.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tuples, continuationToken);
    }

    @Override
    public String toString() {
        return "ReadResponse[" +
                "tuples=" + tuples + ", " +
                "continuationToken=" + continuationToken + ']';
    }

}
