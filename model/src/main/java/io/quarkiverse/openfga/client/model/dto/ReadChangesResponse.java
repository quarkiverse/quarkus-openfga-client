package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.TupleChange;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ReadChangesResponse {

    private final List<TupleChange> changes;

    @JsonProperty("continuation_token")
    @Nullable
    private final String continuationToken;

    @JsonCreator(mode = PROPERTIES)
    public ReadChangesResponse(List<TupleChange> changes,
            @JsonProperty("continuation_token") @Nullable String continuationToken) {
        this.changes = Preconditions.parameterNonNull(changes, "changes");
        this.continuationToken = continuationToken;
    }

    public List<TupleChange> getChanges() {
        return changes;
    }

    @JsonProperty("continuation_token")
    @Nullable
    public String getContinuationToken() {
        return continuationToken;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ReadChangesResponse) obj;
        return Objects.equals(this.changes, that.changes) &&
                Objects.equals(this.continuationToken, that.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changes, continuationToken);
    }

    @Override
    public String toString() {
        return "ReadChangesResponse[" +
                "changes=" + changes + ", " +
                "continuationToken=" + continuationToken + ']';
    }

}
