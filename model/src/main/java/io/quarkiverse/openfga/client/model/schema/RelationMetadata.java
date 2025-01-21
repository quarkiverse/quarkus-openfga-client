package io.quarkiverse.openfga.client.model.schema;

import java.util.*;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.utils.Strings;

public final class RelationMetadata {

    public static RelationMetadata of(@Nullable List<RelationReference> directlyRelatedUserTypes, @Nullable String module,
            @Nullable String sourceInfo) {
        return new RelationMetadata(directlyRelatedUserTypes, module, sourceInfo);
    }

    public static RelationMetadata of(@Nullable List<RelationReference> directlyRelatedUserTypes) {
        return of(directlyRelatedUserTypes, null, null);
    }

    public static RelationMetadata of(RelationReference... directlyRelatedUserTypes) {
        return of(List.of(directlyRelatedUserTypes));
    }

    public static final class Builder {
        @Nullable
        private List<RelationReference> directlyRelatedUserTypes;

        @Nullable
        private String module;

        @Nullable
        private String sourceInfo;

        private Builder() {
        }

        public Builder directlyRelatedUserTypes(@Nullable Collection<RelationReference> directlyRelatedUserTypes) {
            if (directlyRelatedUserTypes == null) {
                return this;
            }
            this.directlyRelatedUserTypes = new ArrayList<>(directlyRelatedUserTypes);
            return this;
        }

        public Builder addDirectlyRelatedUserTypes(@Nullable Collection<RelationReference> directlyRelatedUserTypes) {
            if (directlyRelatedUserTypes == null) {
                return this;
            }
            if (this.directlyRelatedUserTypes == null) {
                this.directlyRelatedUserTypes = new ArrayList<>();
            }
            this.directlyRelatedUserTypes.addAll(directlyRelatedUserTypes);
            return this;
        }

        public Builder addDirectlyRelatedUserTypes(RelationReference... directlyRelatedUserTypes) {
            return addDirectlyRelatedUserTypes(List.of(directlyRelatedUserTypes));
        }

        public Builder addDirectlyRelatedUserType(RelationReference value) {
            if (this.directlyRelatedUserTypes == null) {
                this.directlyRelatedUserTypes = List.of();
            }
            this.directlyRelatedUserTypes.add(value);
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

        public RelationMetadata build() {
            return new RelationMetadata(directlyRelatedUserTypes, module, sourceInfo);
        }
    }

    @Nullable
    private final List<RelationReference> directlyRelatedUserTypes;

    @Nullable
    private final String module;

    @JsonProperty("source_info")
    @Nullable
    private final String sourceInfo;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelationMetadata(@JsonProperty("directly_related_user_types") @Nullable List<RelationReference> directlyRelatedUserTypes,
            @Nullable String module,
            @JsonProperty("source_info") @Nullable String sourceInfo) {
        this.directlyRelatedUserTypes = directlyRelatedUserTypes;
        this.module = Strings.emptyToNull(module);
        this.sourceInfo = Strings.emptyToNull(sourceInfo);
    }

    @JsonProperty("directly_related_user_types")
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
        if (!(o instanceof RelationMetadata that))
            return false;
        return Objects.equals(directlyRelatedUserTypes, that.directlyRelatedUserTypes) &&
                Objects.equals(module, that.module) &&
                Objects.equals(sourceInfo, that.sourceInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directlyRelatedUserTypes, module, sourceInfo);
    }

    @Override
    public String toString() {
        return "RelationMetadata[" +
                "directlyRelatedUserTypes=" + directlyRelatedUserTypes + ", " +
                "module=" + module + ", " +
                "sourceInfo=" + sourceInfo + ']';
    }
}
