package io.quarkiverse.openfga.client.model;

import static io.quarkiverse.openfga.client.model.Schema.*;
import static io.quarkiverse.openfga.client.model.utils.ModelMapper.mapper;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openfga.client.model.Schema.TypeDefinition;
import io.quarkiverse.openfga.client.model.Schema.Userset;

public class AuthorizationModelSchemaTest {

    @Test
    void testUsersetWithThisRead() {
        var json = "{\"this\": {}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var directUserset = userset.self_();
                    assertThat(directUserset)
                            .isNotNull();
                });
    }

    @Test
    void testUsersetWithComputedUsersetRead() {
        var json = "{\"computedUserset\": {\"object\": \"doc:123\", \"relation\": \"admin\"}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var computedUserset = userset.computedUserset();
                    assertThat(computedUserset)
                            .isNotNull()
                            .isEqualTo(objectRelation("doc:123", "admin"));
                });
    }

    @Test
    void testUsersetWithTupleToUsersetRead() {
        var json = "{\"tupleToUserset\":{\"tupleset\":{\"object\": \"a\", \"relation\":\"b\"}, \"computedUserset\":{\"object\": \"c\", \"relation\":\"d\"}}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var tupleToUserset = userset.tupleToUserset();
                    assertThat(tupleToUserset)
                            .isNotNull()
                            .satisfies(t -> {
                                assertThat(t.tupleset())
                                        .isEqualTo(objectRelation("a", "b"));
                                assertThat(t.computedUserset())
                                        .isEqualTo(objectRelation("c", "d"));
                            });
                });
    }

    @Test
    void testUsersetWithUnionRead() {
        var json = "{\"union\": {\"child\":[{\"this\":{}}, {\"computedUserset\": {\"object\": \"b\", \"relation\": \"c\"}}]}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var union = userset.union();
                    assertThat(union)
                            .isNotNull()
                            .satisfies(u -> assertThat(u.child())
                                    .hasSize(2)
                                    .containsExactlyInAnyOrder(
                                            thisUserset(),
                                            computedUserset("b", "c")));
                });
    }

    @Test
    void testUsersetWithIntersectionRead() {
        var json = "{\"intersection\": {\"child\":[{\"this\":{}}, {\"computedUserset\": {\"object\": \"b\", \"relation\": \"c\"}}]}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var intersection = userset.intersection();
                    assertThat(intersection)
                            .isNotNull()
                            .satisfies(i -> assertThat(i.child())
                                    .hasSize(2)
                                    .containsExactlyInAnyOrder(
                                            thisUserset(),
                                            computedUserset("b", "c")));
                });
    }

    @Test
    void testUsersetWithDifferenceRead() {
        var json = "{\"difference\":{\"base\":{\"this\":{}},\"subtract\":{\"this\":{}}}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var difference = userset.difference();
                    assertThat(difference)
                            .isNotNull()
                            .satisfies(d -> {
                                assertThat(d.base())
                                        .isNotNull()
                                        .isEqualTo(thisUserset());
                                assertThat(d.subtract())
                                        .isNotNull()
                                        .isEqualTo(thisUserset());
                            });
                });
    }

    @Test
    void tesTypeDefinitionRead() {
        var json = "{\"type\":\"a\",\"relations\":{\"b\":{\"computedUserset\":{\"object\":\"c\",\"relation\":\"d\"}},\"e\":{\"computedUserset\":{\"object\":\"f\",\"relation\":\"g\"}}}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var typeDefinition = mapper.readValue(json, TypeDefinition.class);
                    assertThat(typeDefinition)
                            .isEqualTo(
                                    TypeDefinition.builder()
                                            .type("a")
                                            .addRelation("b", computedUserset("c", "d"))
                                            .addRelation("e", computedUserset("f", "g"))
                                            .build());
                });
    }

    @Test
    void testTypeDefinitionsRead() {
        var json = "{\"type_definitions\":[{\"type\":\"a\",\"relations\":{\"b\":{\"computedUserset\":{\"object\":\"c\",\"relation\":\"d\"}},\"e\":{\"computedUserset\":{\"object\":\"f\",\"relation\":\"g\"}}}},{\"type\":\"h\",\"relations\":{\"i\":{\"computedUserset\":{\"object\":\"j\",\"relation\":\"k\"}},\"l\":{\"computedUserset\":{\"object\":\"m\",\"relation\":\"n\"}}}}]}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var typeDefinitions = mapper.readValue(json, AuthorizationModelSchema.class);
                    assertThat(typeDefinitions)
                            .isNotNull()
                            .satisfies(t -> assertThat(t.getTypeDefinitions())
                                    .hasSize(2)
                                    .containsExactlyInAnyOrder(
                                            TypeDefinition.builder()
                                                    .type("a")
                                                    .addRelation("b", computedUserset("c", "d"))
                                                    .addRelation("e", computedUserset("f", "g"))
                                                    .build(),
                                            TypeDefinition.builder()
                                                    .type("h")
                                                    .addRelation("i", computedUserset("j", "k"))
                                                    .addRelation("l", computedUserset("m", "n"))
                                                    .build()));
                });
    }

}
