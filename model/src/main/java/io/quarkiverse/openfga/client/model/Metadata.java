package io.quarkiverse.openfga.client.model;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Metadata {
    @Nullable
    Map<String, RelationMetadata> relations;

    @JsonCreator
    public Metadata(@Nullable Map<String, RelationMetadata> relations) {
        this.relations = relations;
    }

    @Nullable
    public Map<String, RelationMetadata> getRelations() {
        return relations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Metadata))
            return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(relations, metadata.relations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relations);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "relations=" + relations +
                '}';
    }
}
