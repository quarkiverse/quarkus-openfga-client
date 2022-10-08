package io.quarkiverse.openfga.client.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class TypeDefinitionsTest {

    ObjectMapper mapper = new JsonMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new ParameterNamesModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Test
    void testUsersetWithThisRead() {
        var json = "{\"this\": {\"any\":\"value\"}}";

        var userset = assertDoesNotThrow(() -> mapper.readValue(json, Userset.class));

        var directUserset = userset.getDirectUserset();
        assertThat(directUserset, notNullValue());
        assertThat(directUserset, hasEntry("any", "value"));
    }

    @Test
    void testUsersetWithComputedUsersetRead() {
        var json = "{\"computedUserset\": {\"object\": \"doc:123\", \"relation\": \"admin\"}}";

        var userset = assertDoesNotThrow(() -> mapper.readValue(json, Userset.class));

        var computedUserset = userset.getComputedUserset();
        assertThat(computedUserset, notNullValue());
        assertThat(computedUserset, equalTo(new ObjectRelation("doc:123", "admin")));
    }

    @Test
    void testUsersetWithTupleToUsersetRead() {
        var json = "{\"tupleToUserset\":{\"tupleset\":{\"object\": \"a\", \"relation\":\"b\"}, \"computedUserset\":{\"object\": \"c\", \"relation\":\"d\"}}}";

        var userset = assertDoesNotThrow(() -> mapper.readValue(json, Userset.class));

        var tupleToUserset = userset.getTupleToUserset();
        assertThat(tupleToUserset, notNullValue());
        assertThat(tupleToUserset.getTupleset(), equalTo(new ObjectRelation("a", "b")));
        assertThat(tupleToUserset.getComputedUserset(), equalTo(new ObjectRelation("c", "d")));
    }

    @Test
    void testUsersetWithUnionRead() {
        var json = "{\"union\": {\"child\":[{\"this\":{\"a\":1}}, {\"computedUserset\": {\"object\": \"b\", \"relation\": \"c\"}}]}}";

        var userset = assertDoesNotThrow(() -> mapper.readValue(json, Userset.class));

        var union = userset.getUnion();
        assertThat(union, notNullValue());

        var child = union.getChild();
        assertThat(child, hasSize(2));
        assertThat(child, hasItem(Userset.direct("a", 1)));
        assertThat(child, hasItem(Userset.computed("b", "c")));
    }

    @Test
    void testUsersetWithIntersectionRead() {
        var json = "{\"intersection\": {\"child\":[{\"this\":{\"a\":1}}, {\"computedUserset\": {\"object\": \"b\", \"relation\": \"c\"}}]}}";

        var userset = assertDoesNotThrow(() -> mapper.readValue(json, Userset.class));

        var intersection = userset.getIntersection();
        assertThat(intersection, notNullValue());

        var child = intersection.getChild();
        assertThat(child, notNullValue());
        assertThat(child, hasSize(2));
        assertThat(child, hasItem(Userset.direct("a", 1)));
        assertThat(child, hasItem(Userset.computed("b", "c")));
    }

    @Test
    void testUsersetWithDifferenceRead() {
        var json = "{\"difference\":{\"base\":{\"this\":{\"a\":1}},\"subtract\":{\"this\":{\"b\":2}}}}";

        var userset = assertDoesNotThrow(() -> mapper.readValue(json, Userset.class));

        var difference = userset.getDifference();
        assertThat(difference, notNullValue());

        var base = difference.getBase();
        assertThat(base, notNullValue());

        var baseDirect = base.getDirectUserset();
        assertThat(baseDirect, hasEntry("a", 1));

        var subtract = difference.getSubtract();
        assertThat(subtract, notNullValue());

        var subtractDirect = subtract.getDirectUserset();
        assertThat(subtractDirect, hasEntry("b", 2));
    }

    @Test
    void tesTypeDefinitionRead() {
        var json = "{\"type\":\"a\",\"relations\":{\"b\":{\"computedUserset\":{\"object\":\"c\",\"relation\":\"d\"}},\"e\":{\"computedUserset\":{\"object\":\"f\",\"relation\":\"g\"}}}}";

        var typeDefinition = assertDoesNotThrow(() -> mapper.readValue(json, TypeDefinition.class));
        assertThat(typeDefinition,
                equalTo(new TypeDefinition("a", Map.of("b", Userset.computed("c", "d"), "e", Userset.computed("f", "g")))));
    }

    @Test
    void testTypeDefinitionsRead() {
        var json = "{\"type_definitions\":[{\"type\":\"a\",\"relations\":{\"b\":{\"computedUserset\":{\"object\":\"c\",\"relation\":\"d\"}},\"e\":{\"computedUserset\":{\"object\":\"f\",\"relation\":\"g\"}}}},{\"type\":\"h\",\"relations\":{\"i\":{\"computedUserset\":{\"object\":\"j\",\"relation\":\"k\"}},\"l\":{\"computedUserset\":{\"object\":\"m\",\"relation\":\"n\"}}}}]}";

        var typeDefinitions = assertDoesNotThrow(() -> mapper.readValue(json, TypeDefinitions.class));
        assertThat(typeDefinitions, notNullValue());

        var typeDefinitionsItems = typeDefinitions.getTypeDefinitions();
        assertThat(typeDefinitionsItems, notNullValue());
        assertThat(typeDefinitionsItems, hasSize(2));
        assertThat(typeDefinitionsItems,
                hasItem(new TypeDefinition("a", Map.of("b", Userset.computed("c", "d"), "e", Userset.computed("f", "g")))));
        assertThat(typeDefinitionsItems,
                hasItem(new TypeDefinition("h", Map.of("i", Userset.computed("j", "k"), "l", Userset.computed("m", "n")))));
    }

    @Test
    void testDirectUsersetBuild() {
        var direct1 = Userset.direct("a", 1)
                .getDirectUserset();
        assertThat(direct1, hasEntry("a", 1));

        var direct2 = Userset.direct("a", 1, "b", 2)
                .getDirectUserset();
        assertThat(direct2, hasEntry("a", 1));
        assertThat(direct2, hasEntry("b", 2));

        var direct3 = Userset.direct("a", 1, "b", 2, "c", 3)
                .getDirectUserset();
        assertThat(direct3, hasEntry("a", 1));
        assertThat(direct3, hasEntry("b", 2));
        assertThat(direct3, hasEntry("c", 3));

        var direct4 = Userset.direct("a", 1, "b", 2, "c", 3, "d", 4)
                .getDirectUserset();
        assertThat(direct4, hasEntry("a", 1));
        assertThat(direct4, hasEntry("b", 2));
        assertThat(direct4, hasEntry("c", 3));
        assertThat(direct4, hasEntry("d", 4));

        var direct5 = Userset.direct("a", 1, "b", 2, "c", 3, "d", 4, "e", 5)
                .getDirectUserset();
        assertThat(direct5, hasEntry("a", 1));
        assertThat(direct5, hasEntry("b", 2));
        assertThat(direct5, hasEntry("c", 3));
        assertThat(direct5, hasEntry("d", 4));
        assertThat(direct5, hasEntry("e", 5));
    }

}
