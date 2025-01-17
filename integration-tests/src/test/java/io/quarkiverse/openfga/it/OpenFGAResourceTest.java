package io.quarkiverse.openfga.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OpenFGAResourceTest {

    @Test
    public void testListStores() {

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
                        "tuple_key.object", containsInAnyOrder("thing:1", "thing:2", "thing:2"),
                        "tuple_key.relation", containsInAnyOrder("owner", "owner", "reader"),
                        "tuple_key.user", containsInAnyOrder("user:me", "user:you", "user:me"),
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
                        "key.object", containsInAnyOrder("thing:1", "thing:2", "thing:2"),
                        "key.relation", containsInAnyOrder("owner", "owner", "reader"),
                        "key.user", containsInAnyOrder("user:me", "user:you", "user:me"));
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
                        "key.object", containsInAnyOrder("thing:1", "thing:2", "thing:2"),
                        "key.relation", containsInAnyOrder("owner", "owner", "reader"),
                        "key.user", containsInAnyOrder("user:me", "user:you", "user:me"));
    }

    @Test
    public void testListAssertions() {

        given()
                .when().get("/openfga/assertions")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(0));
    }
}
