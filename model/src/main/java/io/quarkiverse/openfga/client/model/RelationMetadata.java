package io.quarkiverse.openfga.client.model;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RelationMetadata {

    @JsonProperty("directly_related_user_types")
    @Nullable
    List<RelationReference> directlyRelatedUserTypes;

    @JsonCreator
    public RelationMetadata(@Nullable List<RelationReference> directlyRelatedUserTypes) {
        this.directlyRelatedUserTypes = directlyRelatedUserTypes;
    }

    @Nullable
    public List<RelationReference> getDirectlyRelatedUserTypes() {
        return directlyRelatedUserTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RelationMetadata))
            return false;
        RelationMetadata that = (RelationMetadata) o;
        return Objects.equals(directlyRelatedUserTypes, that.directlyRelatedUserTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directlyRelatedUserTypes);
    }

    @Override
    public String toString() {
        return "RelationMetadata{" +
                "directlyRelatedUserTypes=" + directlyRelatedUserTypes +
                '}';
    }
}
