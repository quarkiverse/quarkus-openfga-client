package io.quarkiverse.openfga.client.model.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Strings;

public final class Metadata {

    public static Metadata of(@Nullable Map<String, RelationMetadata> relations, @Nullable String module,
            @Nullable String sourceInfo) {
        return new Metadata(relations, module, sourceInfo);
    }

    public static Metadata of(@Nullable Map<String, RelationMetadata> relations) {
        return of(relations, null, null);
    }

    public static final class Builder {
        @Nullable
        private Map<String, RelationMetadata> relations;

        @Nullable
        private String module;

        @Nullable
        private String sourceInfo;

        private Builder() {
        }

        public Builder relations(@Nullable Map<String, RelationMetadata> relations) {
            this.relations = relations;
            return this;
        }

        public Builder addRelations(@Nullable Map<String, RelationMetadata> relations) {
            if (relations == null) {
                return this;
            }
            if (this.relations == null) {
                this.relations = new HashMap<>();
            }
            for (var entry : relations.entrySet()) {
                if (this.relations.containsKey(entry.getKey())) {
                    throw new IllegalArgumentException("Duplicate key: " + entry.getKey());
                }
                this.relations.put(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Builder addRelation(String key, RelationMetadata value) {
            if (this.relations == null) {
                this.relations = new HashMap<>();
            }
            if (this.relations.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate key: " + key);
            }
            this.relations.put(key, value);
            return this;
        }

        public Builder module(@Nullable String module) {
            this.module = module;
            return this;
        }

        public Builder sourceInfo(@Nullable String sourceInfo) {
            this.sourceInfo = sourceInfo;
            return this;
        }

        public Metadata build() {
            return new Metadata(relations, module, sourceInfo);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final Map<String, RelationMetadata> relations;

    @Nullable
    private final String module;

    @JsonProperty("source_info")
    @Nullable
    private final String sourceInfo;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Metadata(@Nullable Map<String, RelationMetadata> relations, @Nullable String module, @Nullable String sourceInfo) {
        this.relations = relations;
        this.module = Strings.emptyToNull(module);
        this.sourceInfo = Strings.emptyToNull(sourceInfo);
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
        if (!(o instanceof Metadata metadata))
            return false;
        return Objects.equals(relations, metadata.relations) &&
                Objects.equals(module, metadata.module) &&
                Objects.equals(sourceInfo, metadata.sourceInfo);
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
