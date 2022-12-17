package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ListObjectsResponse {
    @JsonProperty("object_ids")
    private final List<String> objects;

    @JsonCreator(mode = PROPERTIES)
    public ListObjectsResponse(@JsonProperty("objects") List<String> objects) {
        this.objects = Preconditions.parameterNonNull(objects, "objects");
    }

    @JsonProperty("objects")
    public List<String> getObjects() {
        return objects;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ListObjectsResponse) obj;
        return Objects.equals(this.objects, that.objects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objects);
    }

    @Override
    public String toString() {
        return "ListObjectsResponse[" +
                "objects=" + objects + ']';
    }

}
