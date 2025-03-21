package io.quarkiverse.openfga.client.model;

import static io.quarkiverse.openfga.client.model.utils.ModelMapper.mapper;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.collection;
import static org.assertj.core.api.InstanceOfAssertFactories.map;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openfga.client.model.Schema.SourceInfo;
import io.quarkiverse.openfga.client.model.Schema.TypeDefinition;
import io.quarkiverse.openfga.client.model.Schema.TypeDefinition.Metadata;
import io.quarkiverse.openfga.client.model.Schema.TypeDefinition.Metadata.Relation;

public class AuthorizationModelSchemaParseTest {

    @Test
    void testParseSimpleModel() throws Exception {

        try (var json = AuthorizationModelSchemaParseTest.class.getResourceAsStream("/core.json")) {
            assertThatNoException()
                    .isThrownBy(() -> {
                        var model = mapper.readValue(json, AuthorizationModelSchema.class);
                        assertThat(model)
                                .isNotNull()
                                .satisfies(s -> {
                                    assertThat(s)
                                            .extracting(AuthorizationModelSchema::getSchemaVersion)
                                            .isEqualTo("1.1");
                                    assertThat(s)
                                            .extracting(AuthorizationModelSchema::getTypeDefinitions,
                                                    collection(TypeDefinition.class))
                                            .isNotNull()
                                            .hasSize(3)
                                            .satisfies(t -> {
                                                assertThat(t)
                                                        .extracting(TypeDefinition::getType)
                                                        .containsExactlyInAnyOrder("user", "group", "organization");
                                                assertThat(t.stream().filter(td -> td.getType().equals("user")).findAny())
                                                        .get()
                                                        .extracting(TypeDefinition::getMetadata)
                                                        .isNull();
                                                assertThat(
                                                        t.stream().filter(td -> td.getType().equals("organization")).findAny())
                                                        .get()
                                                        .extracting(TypeDefinition::getMetadata)
                                                        .satisfies(md -> {
                                                            assertThat(md)
                                                                    .extracting(Metadata::getRelations,
                                                                            map(String.class, Relation.class))
                                                                    .containsOnlyKeys("admin", "member");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getSourceInfo)
                                                                    .isNull();
                                                            assertThat(md)
                                                                    .extracting(Metadata::getModule)
                                                                    .isNull();

                                                        });
                                                assertThat(t.stream().filter(td -> td.getType().equals("group")).findAny())
                                                        .get()
                                                        .extracting(TypeDefinition::getMetadata)
                                                        .satisfies(md -> {
                                                            assertThat(md)
                                                                    .extracting(Metadata::getRelations,
                                                                            map(String.class, Relation.class))
                                                                    .containsOnlyKeys("member");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getSourceInfo)
                                                                    .isNull();
                                                            assertThat(md)
                                                                    .extracting(Metadata::getModule)
                                                                    .isNull();

                                                        });
                                            });
                                });
                    });
        }
    }

    @Test
    void testParseModularizedModel() throws Exception {

        try (var json = AuthorizationModelSchemaParseTest.class.getResourceAsStream("/modules.json")) {
            assertThatNoException()
                    .isThrownBy(() -> {
                        var model = mapper.readValue(json, AuthorizationModelSchema.class);
                        assertThat(model)
                                .isNotNull()
                                .satisfies(s -> {
                                    assertThat(s)
                                            .extracting(AuthorizationModelSchema::getSchemaVersion)
                                            .isEqualTo("1.2");
                                    assertThat(s)
                                            .extracting(AuthorizationModelSchema::getTypeDefinitions,
                                                    collection(TypeDefinition.class))
                                            .isNotNull()
                                            .hasSize(5)
                                            .satisfies(t -> {
                                                assertThat(t)
                                                        .extracting(TypeDefinition::getType)
                                                        .containsExactlyInAnyOrder("user", "group", "organization", "space",
                                                                "page");

                                                assertThat(t.stream().filter(td -> td.getType().equals("user")).findAny())
                                                        .get()
                                                        .extracting(TypeDefinition::getMetadata)
                                                        .isNotNull()
                                                        .satisfies(md -> {
                                                            assertThat(md)
                                                                    .extracting(Metadata::getRelations)
                                                                    .isNull();
                                                            assertThat(md)
                                                                    .extracting(Metadata::getModule)
                                                                    .isEqualTo("core");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getSourceInfo)
                                                                    .isNotNull()
                                                                    .extracting(SourceInfo::getFile)
                                                                    .isEqualTo("core.fga");
                                                        });

                                                assertThat(
                                                        t.stream().filter(td -> td.getType().equals("organization")).findAny())
                                                        .get()
                                                        .extracting(TypeDefinition::getMetadata)
                                                        .satisfies(md -> {
                                                            assertThat(md)
                                                                    .extracting(Metadata::getRelations,
                                                                            map(String.class, Relation.class))
                                                                    .containsOnlyKeys("admin", "member", "can_create_space");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getModule)
                                                                    .isEqualTo("core");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getSourceInfo)
                                                                    .isNotNull()
                                                                    .extracting(SourceInfo::getFile)
                                                                    .isEqualTo("core.fga");

                                                        });

                                                assertThat(t.stream().filter(td -> td.getType().equals("group")).findAny())
                                                        .get()
                                                        .extracting(TypeDefinition::getMetadata)
                                                        .satisfies(md -> {
                                                            assertThat(md)
                                                                    .extracting(Metadata::getRelations,
                                                                            map(String.class, Relation.class))
                                                                    .containsOnlyKeys("member");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getSourceInfo)
                                                                    .isNotNull()
                                                                    .extracting(SourceInfo::getFile)
                                                                    .isEqualTo("core.fga");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getModule)
                                                                    .isEqualTo("core");
                                                        });

                                                assertThat(t.stream().filter(td -> td.getType().equals("space")).findAny())
                                                        .get()
                                                        .extracting(TypeDefinition::getMetadata)
                                                        .satisfies(md -> {
                                                            assertThat(md)
                                                                    .extracting(Metadata::getRelations,
                                                                            map(String.class, Relation.class))
                                                                    .containsOnlyKeys("can_view_pages", "organization");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getSourceInfo)
                                                                    .isNotNull()
                                                                    .extracting(SourceInfo::getFile)
                                                                    .isEqualTo("wiki.fga");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getModule)
                                                                    .isEqualTo("wiki");
                                                        });

                                                assertThat(t.stream().filter(td -> td.getType().equals("page")).findAny())
                                                        .get()
                                                        .extracting(TypeDefinition::getMetadata)
                                                        .satisfies(md -> {
                                                            assertThat(md)
                                                                    .extracting(Metadata::getRelations,
                                                                            map(String.class, Relation.class))
                                                                    .containsOnlyKeys("owner", "space");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getSourceInfo)
                                                                    .isNotNull()
                                                                    .extracting(SourceInfo::getFile)
                                                                    .isEqualTo("wiki.fga");
                                                            assertThat(md)
                                                                    .extracting(Metadata::getModule)
                                                                    .isEqualTo("wiki");
                                                        });
                                            });
                                });
                    });
        }
    }
}
