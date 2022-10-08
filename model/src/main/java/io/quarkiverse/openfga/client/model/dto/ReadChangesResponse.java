package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.TupleChange;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ReadChangesResponse {
    private final List<TupleChange> changes;

    @JsonCreator(mode = PROPERTIES)
    public ReadChangesResponse(List<TupleChange> changes) {
        this.changes = Preconditions.parameterNonNull(changes, "changes");
    }

    public List<TupleChange> getChanges() {
        return changes;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ReadChangesResponse) obj;
        return Objects.equals(this.changes, that.changes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changes);
    }

    @Override
    public String toString() {
        return "ReadChangesResponse[" +
                "changes=" + changes + ']';
    }

}
