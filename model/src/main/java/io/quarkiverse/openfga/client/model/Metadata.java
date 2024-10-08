package io.quarkiverse.openfga.client.model;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Metadata {

    @Nullable
    private final Map<String, RelationMetadata> relations;

    @Nullable
    private final String module;

    @JsonProperty("source_info")
    @Nullable
    private final String sourceInfo;

    @JsonCreator
    Metadata(@Nullable Map<String, RelationMetadata> relations, @Nullable String module, @Nullable String sourceInfo) {
        this.relations = relations;
        this.module = module;
        this.sourceInfo = sourceInfo;
    }

    public static Metadata of(@Nullable Map<String, RelationMetadata> relations, @Nullable String module,
            @Nullable String sourceInfo) {
        return new Metadata(relations, module, sourceInfo);
    }

    public static Metadata of(@Nullable Map<String, RelationMetadata> relations) {
        return of(relations, null, null);
    }

    @Nullable
    public Map<String, RelationMetadata> getRelations() {
        return relations;
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
        if (!(o instanceof Metadata))
            return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(relations, metadata.relations) && Objects.equals(module, metadata.module)
                && Objects.equals(sourceInfo, metadata.sourceInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relations, module, sourceInfo);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "relations=" + relations + ", " +
                "module=" + module + ", " +
                "sourceInfo=" + sourceInfo +
                '}';
    }
}
