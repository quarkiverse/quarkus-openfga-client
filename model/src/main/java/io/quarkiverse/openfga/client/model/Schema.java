package io.quarkiverse.openfga.client.model;

import java.util.*;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.*;

import io.quarkiverse.openfga.client.model.utils.Preconditions;
import io.quarkiverse.openfga.client.model.utils.Strings;

public interface Schema {

    static TypeDefinition typeDefinition(String type) {
        return TypeDefinition.builder().type(type).build();
    }

    static TypeDefinition.Builder typeDefinition() {
        return TypeDefinition.builder();
    }

    static TypeDefinition.Metadata.Builder typeMetadata() {
        return TypeDefinition.Metadata.builder();
    }

    static TypeDefinition.Metadata.Relation.Builder typeMetadataRelation() {
        return TypeDefinition.Metadata.Relation.builder();
    }

    static TypeDefinition.Metadata.Relation.Reference.Builder typeMetadataRelationReference() {
        return TypeDefinition.Metadata.Relation.Reference.builder();
    }

    final class TypeDefinition {

        public static final class Metadata {

            public static final class Relation {

                public static final class Reference {

                    public static final class Builder {
                        private String type;

                        @Nullable
                        private String relation;

                        @Nullable
                        private java.lang.Object wildcard;

                        @Nullable
                        private String condition;

                        private Builder() {
                        }

                        public Builder type(String type) {
                            this.type = type;
                            return this;
                        }

                        public Builder relation(@Nullable String relation) {
                            this.relation = relation;
                            return this;
                        }

                        public Builder wildcard(@Nullable java.lang.Object wildcard) {
                            this.wildcard = wildcard;
                            return this;
                        }

                        public Builder condition(@Nullable String condition) {
                            this.condition = condition;
                            return this;
                        }

                        public Reference build() {
                            return new Reference(type, relation, wildcard, condition);
                        }
                    }

                    public static Builder builder() {
                        return new Builder();
                    }

                    private final String type;

                    @Nullable
                    private final String relation;

                    @Nullable
                    private final java.lang.Object wildcard;

                    @Nullable
                    private final String condition;

                    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
                    Reference(String type, @Nullable String relation, @Nullable java.lang.Object wildcard,
                            @Nullable String condition) {
                        this.type = Preconditions.parameterNonBlank(type, "type");
                        this.relation = Strings.emptyToNull(relation);
                        this.wildcard = wildcard;
                        this.condition = Strings.emptyToNull(condition);
                    }

                    public String getType() {
                        return type;
                    }

                    @Nullable
                    public String getRelation() {
                        return relation;
                    }

                    @Nullable
                    public java.lang.Object getWildcard() {
                        return wildcard;
                    }

                    @Nullable
                    public String getCondition() {
                        return condition;
                    }

                    @Override
                    public boolean equals(java.lang.Object o) {
                        if (this == o)
                            return true;
                        if (!(o instanceof Reference that))
                            return false;
                        return Objects.equals(type, that.type) &&
                                Objects.equals(relation, that.relation) &&
                                Objects.equals(wildcard, that.wildcard) &&
                                Objects.equals(condition, that.condition);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(type, relation, wildcard, condition);
                    }

                    @Override
                    public String toString() {
                        return "RelationReference[" +
                                "type=" + type + ", " +
                                "relation=" + relation + ", " +
                                "wildcard=" + wildcard + ", " +
                                "condition=" + condition + ']';
                    }
                }

                public static final class Builder {
                    @Nullable
                    private List<Reference> directlyRelatedUserTypes;

                    @Nullable
                    private String module;

                    @Nullable
                    private String sourceInfo;

                    private Builder() {
                    }

                    public Builder directlyRelatedUserTypes(@Nullable Collection<Reference> directlyRelatedUserTypes) {
                        if (directlyRelatedUserTypes == null) {
                            return this;
                        }
                        this.directlyRelatedUserTypes = new ArrayList<>(directlyRelatedUserTypes);
                        return this;
                    }

                    public Builder addDirectlyRelatedUserTypes(@Nullable Collection<Reference> directlyRelatedUserTypes) {
                        if (directlyRelatedUserTypes == null) {
                            return this;
                        }
                        if (this.directlyRelatedUserTypes == null) {
                            this.directlyRelatedUserTypes = new ArrayList<>();
                        }
                        this.directlyRelatedUserTypes.addAll(directlyRelatedUserTypes);
                        return this;
                    }

                    public Builder addDirectlyRelatedUserTypes(Reference... directlyRelatedUserTypes) {
                        return addDirectlyRelatedUserTypes(List.of(directlyRelatedUserTypes));
                    }

                    public Builder module(@Nullable String module) {
                        this.module = module;
                        return this;
                    }

                    public Builder sourceInfo(@Nullable String sourceInfo) {
                        this.sourceInfo = sourceInfo;
                        return this;
                    }

                    public Relation build() {
                        return new Relation(directlyRelatedUserTypes, module, sourceInfo);
                    }
                }

                public static Builder builder() {
                    return new Builder();
                }

                @Nullable
                private final List<Reference> directlyRelatedUserTypes;

                @Nullable
                private final String module;

                @JsonProperty("source_info")
                @Nullable
                private final String sourceInfo;

                @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
                Relation(@JsonProperty("directly_related_user_types") @Nullable List<Reference> directlyRelatedUserTypes,
                        @Nullable String module,
                        @JsonProperty("source_info") @Nullable String sourceInfo) {
                    this.directlyRelatedUserTypes = directlyRelatedUserTypes;
                    this.module = Strings.emptyToNull(module);
                    this.sourceInfo = Strings.emptyToNull(sourceInfo);
                }

                @JsonProperty("directly_related_user_types")
                @Nullable
                public List<Reference> getDirectlyRelatedUserTypes() {
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
                public boolean equals(java.lang.Object o) {
                    if (this == o)
                        return true;
                    if (!(o instanceof Relation that))
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

            public static final class Builder {
                @Nullable
                private Map<String, Relation> relations;

                @Nullable
                private String module;

                @Nullable
                private String sourceInfo;

                private Builder() {
                }

                public Builder relations(@Nullable Map<String, Relation> relations) {
                    this.relations = relations;
                    return this;
                }

                public Builder addRelations(@Nullable Map<String, Relation> relations) {
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

                public Builder addRelation(String key, Relation value) {
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
            private final Map<String, Relation> relations;

            @Nullable
            private final String module;

            @JsonProperty("source_info")
            @Nullable
            private final String sourceInfo;

            @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
            Metadata(@Nullable Map<String, Relation> relations, @Nullable String module, @Nullable String sourceInfo) {
                this.relations = relations;
                this.module = Strings.emptyToNull(module);
                this.sourceInfo = Strings.emptyToNull(sourceInfo);
            }

            @Nullable
            public Map<String, Relation> getRelations() {
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
            public boolean equals(java.lang.Object o) {
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

            public Builder relations(@Nullable Map<String, Schema.Userset> relations) {
                if (relations == null) {
                    this.relations = null;
                    return this;
                }
                return addRelations(relations);
            }

            public Builder addRelations(@Nullable Map<String, Schema.Userset> relations) {
                if (relations == null) {
                    return this;
                }
                if (this.relations == null) {
                    this.relations = new HashMap<>();
                }
                this.relations.putAll(relations);
                return this;
            }

            public Builder addRelation(String relation, Schema.Userset userset) {
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
        private final Map<String, Schema.Userset> relations;
        @Nullable
        private final Metadata metadata;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        TypeDefinition(String type, @Nullable Map<String, Schema.Userset> relations, @Nullable Metadata metadata) {
            this.type = Preconditions.parameterNonNull(type, "type");
            this.relations = Optional.ofNullable(relations).orElseGet(HashMap::new);
            this.metadata = metadata;
        }

        public String getType() {
            return type;
        }

        public Map<String, Schema.Userset> getRelations() {
            return relations;
        }

        @Nullable
        public Metadata getMetadata() {
            return metadata;
        }

        @Override
        public boolean equals(@Nullable java.lang.Object obj) {
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

    static Condition.Builder condition() {
        return Condition.builder();
    }

    static Condition.Parameter.Builder conditionParameter() {
        return Condition.Parameter.builder();
    }

    static Condition.Metadata.Builder conditionMetadata() {
        return Condition.Metadata.builder();
    }

    final class Condition {

        public static final class Parameter {

            public enum TypeName {
                UNSPECIFIED("TYPE_NAME_UNSPECIFIED"),
                ANY("TYPE_NAME_ANY"),
                BOOL("TYPE_NAME_BOOL"),
                STRING("TYPE_NAME_STRING"),
                INT("TYPE_NAME_INT"),
                UINT("TYPE_NAME_UINT"),
                DOUBLE("TYPE_NAME_DOUBLE"),
                DURATION("TYPE_NAME_DURATION"),
                TIMESTAMP("TYPE_NAME_TIMESTAMP"),
                MAP("TYPE_NAME_MAP"),
                LIST("TYPE_NAME_LIST"),
                IPADDRESS("TYPE_NAME_IPADDRESS"),
                UNKNOWN("");

                private final String value;

                TypeName(String value) {
                    this.value = value;
                }

                @JsonValue
                public String getValue() {
                    return value;
                }

                @JsonCreator
                public static TypeName fromValue(String value) {
                    for (var e : TypeName.values()) {
                        if (e.value.equals(value)) {
                            return e;
                        }
                    }
                    return UNKNOWN;
                }
            }

            public static class GenericType extends HashMap<String, java.lang.Object> {

                private GenericType(Map<String, java.lang.Object> map) {
                    super(map);
                }
            }

            public static class Builder {
                private TypeName typeName;
                private Collection<GenericType> genericTypes = new ArrayList<>();

                private Builder() {
                }

                public Builder typeName(TypeName typeName) {
                    this.typeName = typeName;
                    return this;
                }

                public Builder genericTypes(@Nullable Collection<GenericType> genericTypes) {
                    if (genericTypes == null) {
                        this.genericTypes.clear();
                        return this;
                    }
                    if (this.genericTypes == null) {
                        this.genericTypes = new ArrayList<>();
                        return this;
                    }
                    this.genericTypes.addAll(genericTypes);
                    return this;
                }

                public Builder addGenericTypes(@Nullable Collection<GenericType> genericTypes) {
                    if (genericTypes == null) {
                        return this;
                    }
                    if (this.genericTypes == null) {
                        this.genericTypes = new ArrayList<>();
                    }
                    this.genericTypes.addAll(genericTypes);
                    return this;
                }

                public Builder addGenericType(GenericType genericType) {
                    if (this.genericTypes == null) {
                        this.genericTypes = new ArrayList<>();
                    }
                    this.genericTypes.add(genericType);
                    return this;
                }

                public Parameter build() {
                    return new Parameter(typeName, genericTypes);
                }
            }

            public static Builder builder() {
                return new Builder();
            }

            private final TypeName typeName;
            private final Collection<GenericType> genericTypes = new ArrayList<>();

            @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
            Parameter(@JsonProperty("type_name") TypeName typeName,
                    @JsonProperty("generic_types") Collection<GenericType> genericTypes) {
                this.typeName = Preconditions.parameterNonNull(typeName, "typeName");
                this.genericTypes.addAll(Preconditions.parameterNonNull(genericTypes, "genericTypes"));
            }

            @JsonProperty("type_name")
            public TypeName getTypeName() {
                return typeName;
            }

            @JsonProperty("generic_types")
            public Collection<GenericType> getGenericTypes() {
                return genericTypes;
            }

            @Override
            public boolean equals(java.lang.Object obj) {
                if (!(obj instanceof Parameter that))
                    return false;
                return Objects.equals(typeName, that.typeName) && Objects.equals(genericTypes, that.genericTypes);
            }

            @Override
            public int hashCode() {
                return Objects.hash(typeName, genericTypes);
            }

            @Override
            public String toString() {
                return "Parameter[" +
                        "typeName=" + typeName + ", " +
                        "genericTypes=" + genericTypes + ']';
            }
        }

        public static final class Metadata {

            public static final class Builder {

                @Nullable
                private String sourceInfo;
                @Nullable
                private String module;
                private final Map<String, java.lang.Object> extra = new HashMap<>();

                private Builder() {
                }

                public Builder sourceInfo(@Nullable String sourceInfo) {
                    this.sourceInfo = sourceInfo;
                    return this;
                }

                public Builder module(@Nullable String module) {
                    this.module = module;
                    return this;
                }

                public Builder extra(@Nullable Map<String, java.lang.Object> extra) {
                    if (extra == null) {
                        this.extra.clear();
                        return this;
                    }
                    this.extra.putAll(extra);
                    return this;
                }

                public Builder addExtra(@Nullable Map<String, java.lang.Object> extra) {
                    if (extra == null) {
                        return this;
                    }
                    this.extra.putAll(extra);
                    return this;
                }

                public Builder addExtra(String key, java.lang.Object value) {
                    this.extra.put(key, value);
                    return this;
                }

                public Metadata build() {
                    return new Metadata(sourceInfo, module, extra);
                }
            }

            public static Builder builder() {
                return new Builder();
            }

            @Nullable
            private final String sourceInfo;
            @Nullable
            private final String module;
            private final Map<String, java.lang.Object> extra;

            @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
            Metadata(@JsonProperty("source_info") @Nullable String sourceInfo,
                    @JsonProperty("module") @Nullable String module,
                    @JsonAnySetter @Nullable Map<String, java.lang.Object> extra) {
                this.sourceInfo = Strings.emptyToNull(sourceInfo);
                this.module = Strings.emptyToNull(module);
                this.extra = Optional.ofNullable(extra).map(HashMap::new).orElseGet(HashMap::new);
            }

            @JsonProperty("source_info")
            @Nullable
            public String getSourceInfo() {
                return sourceInfo;
            }

            @Nullable
            public String getModule() {
                return module;
            }

            @JsonAnyGetter
            public Map<String, java.lang.Object> getExtra() {
                return extra;
            }

            @Override
            public boolean equals(@Nullable java.lang.Object obj) {
                if (!(obj instanceof Metadata that))
                    return false;
                return Objects.equals(this.sourceInfo, that.sourceInfo) &&
                        Objects.equals(this.module, that.module) &&
                        Objects.equals(this.extra, that.extra);
            }

            @Override
            public int hashCode() {
                return Objects.hash(sourceInfo, module, extra);
            }

            @Override
            public String toString() {
                return "Metadata[" +
                        "sourceInfo=" + sourceInfo + ", " +
                        "module=" + module + "," +
                        "extra=" + extra + ']';
            }
        }

        public static final class Builder {

            private String name;
            private String expression;
            private Map<String, Parameter> parameters = new HashMap<>();
            @Nullable
            private Metadata metadata;

            private Builder() {
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder expression(String expression) {
                this.expression = expression;
                return this;
            }

            public Builder parameters(Map<String, Parameter> parameters) {
                this.parameters = new HashMap<>(parameters);
                return this;
            }

            public Builder addParameters(Map<String, Parameter> parameters) {
                if (this.parameters == null) {
                    this.parameters = new HashMap<>();
                }
                this.parameters.putAll(parameters);
                return this;
            }

            public Builder addParameter(String name, Parameter parameter) {
                if (this.parameters == null) {
                    this.parameters = new HashMap<>();
                }
                this.parameters.put(name, parameter);
                return this;
            }

            public Builder addParameter(String name, Parameter.TypeName typeName) {
                return addParameter(name, Parameter.builder().typeName(typeName).build());
            }

            public Builder addParameter(String name, Parameter.TypeName typeName,
                    Collection<Parameter.GenericType> genericTypes) {
                return addParameter(name, Parameter.builder().typeName(typeName).genericTypes(genericTypes).build());
            }

            public Builder metadata(Metadata metadata) {
                this.metadata = metadata;
                return this;
            }

            public Condition build() {
                return new Condition(name, expression, parameters, metadata);
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        private final String name;
        private final String expression;
        @Nullable
        private final Map<String, Parameter> parameters;
        @Nullable
        private final Metadata metadata;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        Condition(String name, String expression, @Nullable Map<String, Parameter> parameters,
                @Nullable Metadata metadata) {
            this.name = Preconditions.parameterNonBlank(name, "name");
            this.expression = Preconditions.parameterNonBlank(expression, "expression");
            this.parameters = Optional.ofNullable(parameters).map(HashMap::new).orElseGet(HashMap::new);
            this.metadata = metadata;
        }

        public String getName() {
            return name;
        }

        public String getExpression() {
            return expression;
        }

        @Nullable
        public Map<String, Parameter> getParameters() {
            return parameters;
        }

        @Nullable
        public Metadata getMetadata() {
            return metadata;
        }

        @Override
        public boolean equals(@Nullable java.lang.Object obj) {
            if (!(obj instanceof Condition that))
                return false;
            return Objects.equals(name, that.name) &&
                    Objects.equals(expression, that.expression) &&
                    Objects.equals(parameters, that.parameters) &&
                    Objects.equals(metadata, that.metadata);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, expression, parameters, metadata);
        }

        @Override
        public String toString() {
            return "Condition[" +
                    "name=" + name + ", " +
                    "expression=" + expression + ", " +
                    "parameters=" + parameters + ", " +
                    "metadata=" + metadata + ']';
        }
    }

    static Object object(String type, String id) {
        return new Object(type, id);
    }

    record Object(String type, String id) {
        public Object {
            Preconditions.parameterNonBlank("type", type);
            Preconditions.parameterNonBlank("id", id);
        }

        public RelObject asRel() {
            return RelObject.of(type, id);
        }
    }

    static TypedWildcard wildcard(String type) {
        return new TypedWildcard(type);
    }

    record TypedWildcard(String type) {
        public TypedWildcard {
            Preconditions.parameterNonBlank("type", type);
        }

        public RelObjectType asRel() {
            return RelObjectType.of(type);
        }
    }

    static UsersetUser usersetUser(String type, String id, String relation) {
        return new UsersetUser(type, id, relation);
    }

    record UsersetUser(String type, String id, String relation) {
        public UsersetUser {
            Preconditions.parameterNonBlank("type", type);
            Preconditions.parameterNonBlank("id", id);
            Preconditions.parameterNonBlank("relation", relation);
        }

        public RelUser asRel() {
            return RelUser.of(type, id, relation);
        }
    }

    static User user(Object object) {
        return new User(object, null, null);
    }

    static User user(String type, String id) {
        return new User(object(type, id), null, null);
    }

    static User user(UsersetUser userset) {
        return new User(null, userset, null);
    }

    static User user(String type, String id, String relation) {
        return user(new UsersetUser(type, id, relation));
    }

    static User user(TypedWildcard wildcard) {
        return new User(null, null, wildcard);
    }

    static User user(String type) {
        return new User(null, null, new TypedWildcard(type));
    }

    record User(@Nullable Object object, @Nullable UsersetUser userset, @Nullable TypedWildcard wildcard) {
        public User {
            Preconditions.oneOfNonNull("User must have exactly one of object, userset, or wildcard",
                    object, userset, wildcard);
        }

        public RelTyped asRel() {
            if (object != null) {
                return RelUser.of(object.type(), object.id());
            } else if (userset != null) {
                return userset.asRel();
            } else if (wildcard != null) {
                return wildcard.asRel();
            }
            throw new IllegalStateException("User contains no object, userset, or wildcard");
        }
    }

    record Users(Collection<String> users) {
        public Users {
            Preconditions.parameterNonNull(users, "users");
        }

        public Collection<RelUser> asRel() {
            return users.stream().map(RelUser::valueOf).toList();
        }
    }

    static Userset thisUserset() {
        return new Userset(DirectUserset.instance(), null, null, null, null, null);
    }

    static Userset computedUserset(String object, String relation) {
        return new Userset(null, objectRelation(object, relation), null, null, null, null);
    }

    static Userset unionUserset(Collection<Userset> child) {
        return new Userset(null, null, null, new Usersets(child), null, null);
    }

    static Userset intersectionUserset(Collection<Userset> child) {
        return new Userset(null, null, null, null, new Usersets(child), null);
    }

    static Userset tupleToUserset(ObjectRelation tupleset, ObjectRelation computedUserset) {
        return new Userset(null, null, new V1.TupleToUserset(tupleset, computedUserset), null, null, null);
    }

    static Userset differenceUserset(Userset base, Userset subtract) {
        return new Userset(null, null, null, null, null, new V1.Difference(base, subtract));
    }

    record Userset(@JsonProperty("this") @Nullable DirectUserset self_, @Nullable ObjectRelation computedUserset,
            @Nullable V1.TupleToUserset tupleToUserset, @Nullable Usersets union,
            @Nullable Usersets intersection, @Nullable V1.Difference difference) {
    }

    static Usersets usersets(Collection<Userset> child) {
        return new Usersets(child);
    }

    record Usersets(Collection<Userset> child) {
        public Usersets {
            Preconditions.parameterNonNull(child, "child");
        }
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    class DirectUserset {
        @SuppressWarnings("InstantiationOfUtilityClass")
        private static final DirectUserset INSTANCE = new DirectUserset();

        @JsonCreator
        public static DirectUserset instance() {
            return INSTANCE;
        }
    }

    static ObjectRelation objectRelation(String object, String relation) {
        return new ObjectRelation(object, relation);
    }

    record ObjectRelation(@Nullable String object, @Nullable String relation) {
        public ObjectRelation(@Nullable String object, @Nullable String relation) {
            this.object = Strings.emptyToNull(object);
            this.relation = Strings.emptyToNull(relation);
        }
    }

    static UsersetTree usersetTree(UsersetTree.Node root) {
        return new UsersetTree(root);
    }

    static UsersetTree.Node usersetTreeNode(String name, UsersetTree.Leaf leaf) {
        return new UsersetTree.Node(name, leaf, null, null, null);
    }

    static UsersetTree.Node usersetTreeNode(String name, UsersetTree.Difference difference) {
        return new UsersetTree.Node(name, null, difference, null, null);
    }

    static UsersetTree.Node usersetTreeNodeUnion(String name, UsersetTree.Nodes union) {
        return new UsersetTree.Node(name, null, null, union, null);
    }

    static UsersetTree.Node usersetTreeNodeIntersection(String name, UsersetTree.Nodes intersection) {
        return new UsersetTree.Node(name, null, null, null, intersection);
    }

    static UsersetTree.Leaf usersetTreeLeaf(Users users) {
        return new UsersetTree.Leaf(users, null, null);
    }

    static UsersetTree.Leaf usersetTreeLeaf(Collection<String> users) {
        return new UsersetTree.Leaf(new Users(users), null, null);
    }

    static UsersetTree.Leaf usersetTreeLeaf(String computedUserset) {
        return new UsersetTree.Leaf(null, new UsersetTree.Computed(computedUserset), null);
    }

    static UsersetTree.Leaf usersetTreeLeaf(String tupleset, Collection<UsersetTree.Computed> computed) {
        return new UsersetTree.Leaf(null, null, new UsersetTree.TupleToUserset(tupleset, computed));
    }

    static UsersetTree.Node usersetTreeLeafNode(String name, Collection<String> users) {
        return usersetTreeNode(name, usersetTreeLeaf(users));
    }

    static UsersetTree.Node usersetTreeLeafNode(String name, String computedUserset) {
        return usersetTreeNode(name, usersetTreeLeaf(computedUserset));
    }

    static UsersetTree.Node usersetTreeLeafNode(String name, String tupleset, Collection<UsersetTree.Computed> computed) {
        return usersetTreeNode(name, usersetTreeLeaf(tupleset, computed));
    }

    static UsersetTree.Difference usersetTreeDifference(UsersetTree.Node base, UsersetTree.Node subtract) {
        return new UsersetTree.Difference(base, subtract);
    }

    static UsersetTree.Nodes usersetTreeNodes(Collection<? extends UsersetTree.Node> nodes) {
        return new UsersetTree.Nodes(nodes);
    }

    static UsersetTree.Node usersetTreeDifferenceNode(String name, UsersetTree.Node base, UsersetTree.Node subtract) {
        return usersetTreeNode(name, usersetTreeDifference(base, subtract));
    }

    static UsersetTree.Node usersetTreeUnionNode(String name, Collection<? extends UsersetTree.Node> nodes) {
        return usersetTreeNodeUnion(name, new UsersetTree.Nodes(nodes));
    }

    static UsersetTree.Node usersetTreeIntersectionNode(String name, Collection<? extends UsersetTree.Node> nodes) {
        return usersetTreeNodeIntersection(name, new UsersetTree.Nodes(nodes));
    }

    record UsersetTree(@Nullable Node root) {

        public record Node(String name, @Nullable Leaf leaf, @Nullable Difference difference, @Nullable Nodes union,
                @Nullable Nodes intersection) {
        }

        public record Leaf(@Nullable Users users, @Nullable Computed computed, @Nullable TupleToUserset tupleToUserset) {
        }

        public record Computed(String userset) {
            public Computed {
                Preconditions.parameterNonBlank("userset", userset);
            }
        }

        public record TupleToUserset(String tupleset, Collection<Computed> computed) {
            public TupleToUserset {
                Preconditions.parameterNonBlank("tupleset", tupleset);
                Preconditions.parameterNonNull(computed, "computed");
            }
        }

        public record Difference(Node base, Node subtract) {
            public Difference {
                Preconditions.parameterNonNull(base, "base");
                Preconditions.parameterNonNull(subtract, "subtract");
            }
        }

        public record Nodes(Collection<? extends Node> nodes) {
            public Nodes {
                Preconditions.parameterNonNull(nodes, "nodes");
            }
        }

    }

    interface V1 {

        record TupleToUserset(ObjectRelation tupleset, ObjectRelation computedUserset) {
            public TupleToUserset {
                Preconditions.parameterNonNull(tupleset, "tupleset");
                Preconditions.parameterNonNull(computedUserset, "computedUserset");
            }
        }

        record Difference(Userset base, Userset subtract) {
            public Difference {
                Preconditions.parameterNonNull(base, "base");
                Preconditions.parameterNonNull(subtract, "subtract");
            }
        }
    }

}
