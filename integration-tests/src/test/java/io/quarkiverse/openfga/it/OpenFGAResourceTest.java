package io.quarkiverse.openfga.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OpenFGAResourceTest {

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
                        "key.user", containsInAnyOrder("me", "you", "me"));
    }
}
