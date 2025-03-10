package io.quarkiverse.openfga.test;

import static io.quarkiverse.openfga.client.model.Schema.*;

import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.Schema.Condition.Parameter.TypeName;

public final class SchemaFixtures {

    public static final class ObjectTypeNames {
        public static final String USER = "user";
        public static final String GROUP = "group";
        public static final String DOCUMENT = "document";
        public static final String OTHER_DOCUMENT = "other-document";
    }

    public static final class ConditionNames {
        public static final String NON_EXPIRED_GRANT = "non_expired_grant";
    }

    public static final class ParameterNames {
        public static final String CURRENT_TIME = "current_time";
        public static final String GRANT_TIME = "grant_time";
        public static final String GRANT_DURATION = "grant_duration";
    }

    public static final class RelationshipNames {
        public static final String READER = "reader";
        public static final String WRITER = "writer";
        public static final String OWNER = "owner";
        public static final String GRANTEE = "grantee";
    }

    public static final TypeDefinition userTypeDef = typeDefinition(ObjectTypeNames.USER);
    public static final TypeDefinition groupTypeDef = typeDefinition(ObjectTypeNames.GROUP);
    public static final TypeDefinition documentTypeDef = typeDefinition()
            .type(ObjectTypeNames.DOCUMENT)
            .addRelation(RelationshipNames.READER, thisUserset())
            .addRelation(RelationshipNames.WRITER, thisUserset())
            .addRelation(RelationshipNames.OWNER, thisUserset())
            .addRelation(RelationshipNames.GRANTEE, thisUserset())
            .metadata(
                    typeMetadata()
                            .addRelation(
                                    RelationshipNames.READER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build(),
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.GROUP)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.WRITER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.OWNER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.GRANTEE,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build())
                                            .build())
                            .build())
            .build();

    public static final TypeDefinition otherDocumentTypeDef = typeDefinition()
            .type(ObjectTypeNames.OTHER_DOCUMENT)
            .addRelation(RelationshipNames.READER, thisUserset())
            .addRelation(RelationshipNames.WRITER, thisUserset())
            .addRelation(RelationshipNames.OWNER, thisUserset())
            .addRelation(RelationshipNames.GRANTEE, thisUserset())
            .metadata(
                    typeMetadata()
                            .addRelation(
                                    RelationshipNames.READER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build(),
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.GROUP)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.WRITER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.OWNER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.GRANTEE,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build())
                                            .build())
                            .build())
            .build();

    public static final TypeDefinition documentWithConditionTypeDef = typeDefinition()
            .type(ObjectTypeNames.DOCUMENT)
            .addRelation(RelationshipNames.READER, thisUserset())
            .addRelation(RelationshipNames.WRITER, thisUserset())
            .addRelation(RelationshipNames.OWNER, thisUserset())
            .addRelation(RelationshipNames.GRANTEE, thisUserset())
            .metadata(
                    typeMetadata()
                            .addRelation(
                                    RelationshipNames.READER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build(),
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.GROUP)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.WRITER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.OWNER,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .build())
                                            .build())
                            .addRelation(
                                    RelationshipNames.GRANTEE,
                                    typeMetadataRelation()
                                            .addDirectlyRelatedUserTypes(
                                                    typeMetadataRelationReference()
                                                            .type(ObjectTypeNames.USER)
                                                            .condition(ConditionNames.NON_EXPIRED_GRANT)
                                                            .build())
                                            .build())
                            .build())
            .build();

    public static final AuthorizationModelSchema schema = AuthorizationModelSchema.builder()
            .addTypeDefinitions(userTypeDef, groupTypeDef, documentTypeDef, otherDocumentTypeDef)
            .build();

    public static final AuthorizationModelSchema schemaWithCondition = AuthorizationModelSchema.builder()
            .addTypeDefinitions(userTypeDef, groupTypeDef, documentWithConditionTypeDef, otherDocumentTypeDef)
            .addCondition(
                    ConditionNames.NON_EXPIRED_GRANT,
                    condition()
                            .name(ConditionNames.NON_EXPIRED_GRANT)
                            .expression("%s < %s + %s"
                                    .formatted(ParameterNames.CURRENT_TIME, ParameterNames.GRANT_TIME,
                                            ParameterNames.GRANT_DURATION))
                            .addParameter("current_time", TypeName.TIMESTAMP)
                            .addParameter("grant_time", TypeName.TIMESTAMP)
                            .addParameter("grant_duration", TypeName.DURATION)
                            .build())
            .build();

    public static final RelObjectType userType = RelObjectType.of(ObjectTypeNames.USER);
    public static final RelUser userMe = userType.toUser("me");
    public static final RelUser userYou = userType.toUser("you");

    public static final RelObjectType groupType = RelObjectType.of(ObjectTypeNames.GROUP);
    public static final RelUser groupUs = groupType.toUser("us");

    public static final RelObjectType documentType = RelObjectType.of(ObjectTypeNames.DOCUMENT);
    public static final RelObject document123 = documentType.toObject("123");
    public static final RelObject document456 = documentType.toObject("456");
    public static final RelObject document789 = documentType.toObject("789");

    public static final RelObjectType otherDocumentType = RelObjectType.of(ObjectTypeNames.OTHER_DOCUMENT);
    public static final RelObject otherDocument123 = otherDocumentType.toObject("123");
    public static final RelObject otherDocument456 = otherDocumentType.toObject("456");

    private SchemaFixtures() {
    }
}
