package io.quarkiverse.openfga.client.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.quarkiverse.openfga.client.model.schema.ObjectRelation;
import io.quarkiverse.openfga.client.model.schema.Userset;

public class AuthorizationModelSchemaTest {

    ObjectMapper mapper = new JsonMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new ParameterNamesModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Test
    void testUsersetWithThisRead() {
        var json = "{\"this\": {\"any\":\"value\"}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var directUserset = userset.getDirectUserset();
                    assertThat(directUserset)
                            .isNotNull()
                            .containsEntry("any", "value");
                });
    }

    @Test
    void testUsersetWithComputedUsersetRead() {
        var json = "{\"computedUserset\": {\"object\": \"doc:123\", \"relation\": \"admin\"}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var computedUserset = userset.getComputedUserset();
                    assertThat(computedUserset)
                            .isNotNull()
                            .isEqualTo(ObjectRelation.of("doc:123", "admin"));
                });
    }

    @Test
    void testUsersetWithTupleToUsersetRead() {
        var json = "{\"tupleToUserset\":{\"tupleset\":{\"object\": \"a\", \"relation\":\"b\"}, \"computedUserset\":{\"object\": \"c\", \"relation\":\"d\"}}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var tupleToUserset = userset.getTupleToUserset();
                    assertThat(tupleToUserset)
                            .isNotNull()
                            .satisfies(t -> {
                                assertThat(t.tupleset())
                                        .isEqualTo(ObjectRelation.of("a", "b"));
                                assertThat(t.computedUserset())
                                        .isEqualTo(ObjectRelation.of("c", "d"));
                            });
                });
    }

    @Test
    void testUsersetWithUnionRead() {
        var json = "{\"union\": {\"child\":[{\"this\":{\"a\":1}}, {\"computedUserset\": {\"object\": \"b\", \"relation\": \"c\"}}]}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var union = userset.getUnion();
                    assertThat(union)
                            .isNotNull()
                            .satisfies(u -> assertThat(u.getChild())
                                    .hasSize(2)
                                    .containsExactlyInAnyOrder(
                                            Userset.direct("a", 1),
                                            Userset.computed("b", "c")));
                });
    }

    @Test
    void testUsersetWithIntersectionRead() {
        var json = "{\"intersection\": {\"child\":[{\"this\":{\"a\":1}}, {\"computedUserset\": {\"object\": \"b\", \"relation\": \"c\"}}]}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var intersection = userset.getIntersection();
                    assertThat(intersection)
                            .isNotNull()
                            .satisfies(i -> assertThat(i.getChild())
                                    .hasSize(2)
                                    .containsExactlyInAnyOrder(
                                            Userset.direct("a", 1),
                                            Userset.computed("b", "c")));
                });
    }

    @Test
    void testUsersetWithDifferenceRead() {
        var json = "{\"difference\":{\"base\":{\"this\":{\"a\":1}},\"subtract\":{\"this\":{\"b\":2}}}}";

        assertThatNoException()
                .isThrownBy(() -> {
                    var userset = mapper.readValue(json, Userset.class);
                    var difference = userset.getDifference();
                    assertThat(difference)
                            .isNotNull()
                            .satisfies(d -> {
                                assertThat(d.base())
                                        .isNotNull()
                                        .isEqualTo(Userset.direct("a", 1));
                                assertThat(d.subtract())
                                        .isNotNull()
                                        .isEqualTo(Userset.direct("b", 2));
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
                                            .addRelation("b", Userset.computed("c", "d"))
                                            .addRelation("e", Userset.computed("f", "g"))
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
                                                    .addRelation("b", Userset.computed("c", "d"))
                                                    .addRelation("e", Userset.computed("f", "g"))
                                                    .build(),
                                            TypeDefinition.builder()
                                                    .type("h")
                                                    .addRelation("i", Userset.computed("j", "k"))
                                                    .addRelation("l", Userset.computed("m", "n"))
                                                    .build()));
                });
    }

    @Test
    void testDirectUsersetBuild() {
        var direct1 = Userset.direct("a", 1)
                .getDirectUserset();
        assertThat(direct1).containsEntry("a", 1);

        var direct2 = Userset.direct("a", 1, "b", 2)
                .getDirectUserset();
        assertThat(direct2)
                .containsEntry("a", 1)
                .containsEntry("b", 2);

        var direct3 = Userset.direct("a", 1, "b", 2, "c", 3)
                .getDirectUserset();
        assertThat(direct3)
                .containsEntry("a", 1)
                .containsEntry("b", 2)
                .containsEntry("c", 3);

        var direct4 = Userset.direct("a", 1, "b", 2, "c", 3, "d", 4)
                .getDirectUserset();
        assertThat(direct4)
                .containsEntry("a", 1)
                .containsEntry("b", 2)
                .containsEntry("c", 3)
                .containsEntry("d", 4);

        var direct5 = Userset.direct("a", 1, "b", 2, "c", 3, "d", 4, "e", 5)
                .getDirectUserset();
        assertThat(direct5)
                .containsEntry("a", 1)
                .containsEntry("b", 2)
                .containsEntry("c", 3)
                .containsEntry("d", 4)
                .containsEntry("e", 5);
    }

}
