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
    @Nullable
    String module;
    @JsonProperty("source_info")
    @Nullable
    String sourceInfo;

    @JsonCreator
    public RelationMetadata(@Nullable List<RelationReference> directlyRelatedUserTypes, @Nullable String module,
            @Nullable String sourceInfo) {
        this.directlyRelatedUserTypes = directlyRelatedUserTypes;
        this.module = module;
        this.sourceInfo = sourceInfo;
    }

    public RelationMetadata(@Nullable List<RelationReference> directlyRelatedUserTypes) {
        this(directlyRelatedUserTypes, null, null);
    }

    @Nullable
    public List<RelationReference> getDirectlyRelatedUserTypes() {
        return directlyRelatedUserTypes;
    }

    @Nullable
    public String getModule() {
        return module;
    }

    @JsonProperty("source_info")
    @Nullable
    public String getSourceInfo() {
        return sourceInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RelationMetadata))
            return false;
        RelationMetadata that = (RelationMetadata) o;
        return Objects.equals(directlyRelatedUserTypes, that.directlyRelatedUserTypes)
                && Objects.equals(module, that.module) && Objects.equals(sourceInfo, that.sourceInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directlyRelatedUserTypes, module, sourceInfo);
    }

    @Override
    public String toString() {
        return "RelationMetadata{" +
                "directlyRelatedUserTypes=" + directlyRelatedUserTypes + ", " +
                "module=" + module + ", " +
                "sourceInfo=" + sourceInfo +
                '}';
    }
}
