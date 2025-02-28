package io.quarkiverse.openfga.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.eclipse.microprofile.config.ConfigProvider.getConfig;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openfga.client.AssertionsClient;
import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.RelTupleKey;
import io.quarkus.arc.Arc;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SharedTest {

    public static void initAssertions(AssertionsClient assertionsClient) {
        var doc1 = RelObject.of("doc", "a-simple.txt");
        var user = RelObject.of("user", "tester2");
        assertionsClient.update(
                List.of(
                        Assertion.of(
                                RelTupleKey.builder()
                                        .object(doc1)
                                        .relation("can_read")
                                        .user(user)
                                        .build(),
                                true),
                        Assertion.of(
                                RelTupleKey.builder()
                                        .object(doc1)
                                        .relation("can_write")
                                        .user(user)
                                        .build(),
                                false)))
                .await().atMost(Duration.ofSeconds(3));
    }

    public static boolean getFlag(String key, boolean flagDefault) {
        return getConfig().getOptionalValue(key, Boolean.class).orElse(flagDefault);
    }

    @BeforeAll
    public static void beforeAll() {
        if (getFlag("test.init-assertions", true)) {
            try (var client = Arc.container().instance(AssertionsClient.class)) {
                initAssertions(client.get());
            }
        }
    }

    @Test
    public void testListStores() {
        assumeThat(getFlag("test.list-stores-allowed", true))
                .isTrue();

        given()
                .when().get("/openfga/stores")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(1));
    }

    @Test
    public void testReadChanges() {

        given()
                .when().get("/openfga/changes")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(3),
                        "tuple_key.object", containsInAnyOrder("doc:a-simple.txt", "doc:a-simple.txt", "doc:other.pdf"),
                        "tuple_key.relation", containsInAnyOrder("owner", "owner", "viewer"),
                        "tuple_key.user", containsInAnyOrder("user:tester1", "user:tester2", "user:tester2"),
                        "operation",
                        containsInAnyOrder("TUPLE_OPERATION_WRITE", "TUPLE_OPERATION_WRITE", "TUPLE_OPERATION_WRITE"));
    }

    @Test
    public void testListModels() {

        given()
                .when().get("/openfga/authorization-models")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(1));
    }

    @Test
    public void testListTuples() {

        given()
                .when().get("/openfga/authorization-tuples")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(3),
                        "key.object", containsInAnyOrder("doc:a-simple.txt", "doc:other.pdf", "doc:a-simple.txt"),
                        "key.relation", containsInAnyOrder("owner", "owner", "viewer"),
                        "key.user", containsInAnyOrder("user:tester1", "user:tester2", "user:tester2"));
    }

    @Test
    public void testListObjects() {

        given()
                .when().get("/openfga/objects")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(3),
                        "key.object", containsInAnyOrder("doc:a-simple.txt", "doc:other.pdf", "doc:a-simple.txt"),
                        "key.relation", containsInAnyOrder("owner", "owner", "viewer"),
                        "key.user", containsInAnyOrder("user:tester1", "user:tester2", "user:tester2"));
    }

    @Test
    public void testListAssertions() {

        given()
                .when().get("/openfga/assertions")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(2));
    }
}
