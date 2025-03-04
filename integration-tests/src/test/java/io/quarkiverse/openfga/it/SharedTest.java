package io.quarkiverse.openfga.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.eclipse.microprofile.config.ConfigProvider.getConfig;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.json.JsonMapper;

import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.RelTupleKey;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SharedTest {

    @TestHTTPResource()
    public URL endpointURI;

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
    public void testListAssertions() throws Exception {
        if (getFlag("test.init-assertions", true)) {
            initAssertions();
        }

        given()
                .when().get("/openfga/assertions")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(2));
    }

    public void initAssertions() throws Exception {
        var mapper = new JsonMapper();
        var doc1 = RelObject.of("doc", "a-simple.txt");
        var user = RelObject.of("user", "tester2");
        var assertions = List.of(
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
                        false));
        HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .uri(endpointURI.toURI().resolve("/openfga/assertions"))
                                .header("Content-Type", "application/json")
                                .PUT(BodyPublishers.ofString(mapper.writeValueAsString(assertions)))
                                .build(),
                        BodyHandlers.discarding());
    }

    public static boolean getFlag(String key, boolean flagDefault) {
        return getConfig().getOptionalValue(key, Boolean.class).orElse(flagDefault);
    }
}
