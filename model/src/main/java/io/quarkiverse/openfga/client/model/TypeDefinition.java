package io.quarkiverse.openfga.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.schema.Metadata;
import io.quarkiverse.openfga.client.model.schema.Userset;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class TypeDefinition {

    public static TypeDefinition of(String type) {
        return builder().type(type).build();
    }

    public static final class Builder {
        private String type;
        @Nullable
        private Map<String, Userset> relations;
        @Nullable
        private Metadata metadata;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder relations(@Nullable Map<String, Userset> relations) {
            if (relations == null) {
                this.relations = null;
                return this;
            }
            return addRelations(relations);
        }

        public Builder addRelations(@Nullable Map<String, Userset> relations) {
            if (relations == null) {
                return this;
            }
            if (this.relations == null) {
                this.relations = new HashMap<>();
            }
            this.relations.putAll(relations);
            return this;
        }

        public Builder addRelation(String relation, Userset userset) {
            if (this.relations == null) {
                this.relations = new HashMap<>();
            }
            this.relations.put(relation, userset);
            return this;
        }

        public Builder metadata(@Nullable Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public TypeDefinition build() {
            return new TypeDefinition(type, relations, metadata);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final String type;
    private final Map<String, Userset> relations;
    @Nullable
    private final Metadata metadata;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    TypeDefinition(String type, @Nullable Map<String, Userset> relations, @Nullable Metadata metadata) {
        this.type = Preconditions.parameterNonNull(type, "type");
        this.relations = Optional.ofNullable(relations).orElseGet(HashMap::new);
        this.metadata = metadata;
    }

    public String getType() {
        return type;
    }

    public Map<String, Userset> getRelations() {
        return relations;
    }

    @Nullable
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof TypeDefinition that))
            return false;
        return Objects.equals(type, that.type) &&
                Objects.equals(relations, that.relations) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relations, metadata);
    }

    @Override
    public String toString() {
        return "TypeDefinition[" +
                "type=" + type + ", " +
                "relations=" + relations + ", " +
                "metadata=" + metadata + ']';
    }
}
