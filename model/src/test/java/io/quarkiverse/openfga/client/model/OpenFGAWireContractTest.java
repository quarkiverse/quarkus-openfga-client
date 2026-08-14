package io.quarkiverse.openfga.client.model;

import static io.quarkiverse.openfga.client.model.utils.ModelMapper.mapper;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkiverse.openfga.client.model.dto.BatchCheckRequest;
import io.quarkiverse.openfga.client.model.dto.ExpandRequest;
import io.quarkiverse.openfga.client.model.dto.ReadRequest;
import io.quarkiverse.openfga.client.model.dto.StreamedListObjectsResponse;
import io.quarkiverse.openfga.client.model.dto.WriteAssertionsRequest;
import io.quarkiverse.openfga.client.model.dto.WriteRequest;

/**
 * Verifies request shapes against openfga/api commit 6981fff8d33bee21dd9a2001608e6d6c5f553977.
 */
@SuppressWarnings("removal")
public class OpenFGAWireContractTest {

    @Test
    void readOmitsCompatibilityAuthorizationModelIdAndIncludesConsistency() throws Exception {
        var request = ReadRequest.builder()
                .authorizationModelId("01MODEL")
                .consistency(ConsistencyPreference.HIGHER_CONSISTENCY)
                .build();

        var json = json(request);

        assertThat(json.has("authorization_model_id")).isFalse();
        assertThat(json.path("consistency").textValue()).isEqualTo("HIGHER_CONSISTENCY");
    }

    @Test
    void expandOmitsCompatibilityContext() throws Exception {
        var request = ExpandRequest.builder()
                .tupleKey(RelPartialTupleKey.builder()
                        .object(RelObject.of("document", "roadmap"))
                        .relation("viewer")
                        .build())
                .context(Map.of("region", "eu"))
                .build();

        var json = json(request);

        assertThat(json.has("context")).isFalse();
    }

    @Test
    void assertionsOmitPathParameterAndPreserveOnlySupportedConditions() throws Exception {
        var conditionalTuple = conditionalTuple();
        var request = WriteAssertionsRequest.builder()
                .authorizationModelId("01MODEL")
                .assertions(List.of(Assertion.of(conditionalTuple, true, List.of(conditionalTuple),
                        Map.of("region", "eu"))))
                .build();

        var json = json(request);
        var assertion = json.path("assertions").get(0);

        assertThat(json.has("authorization_model_id")).isFalse();
        assertThat(assertion.path("tuple_key").has("condition")).isFalse();
        assertThat(assertion.path("contextual_tuples").get(0).path("condition").path("name").textValue())
                .isEqualTo("in_region");
        assertThat(assertion.path("context").path("region").textValue()).isEqualTo("eu");
    }

    @Test
    void batchCheckOmitsConditionsFromCheckTupleKeys() throws Exception {
        var request = BatchCheckRequest.builder()
                .checks(List.of(Check.builder()
                        .tupleKey(conditionalTuple())
                        .correlationId("check-1")
                        .build()))
                .build();

        var json = json(request);

        assertThat(json.path("checks").get(0).path("tuple_key").has("condition")).isFalse();
    }

    @Test
    void writeNestsConflictBehaviorAndPreservesOnlyWriteConditions() throws Exception {
        var conditionalTuple = conditionalTuple();
        var request = WriteRequest.builder()
                .authorizationModelId("01MODEL")
                .writes(WriteRequest.Writes.of(List.of(conditionalTuple)))
                .deletes(WriteRequest.Deletes.of(List.of(conditionalTuple)))
                .onDuplicate(WriteConflictBehavior.IGNORE)
                .onMissing(WriteConflictBehavior.IGNORE)
                .build();

        var json = json(request);

        assertThat(json.path("writes").path("on_duplicate").textValue()).isEqualTo("ignore");
        assertThat(json.path("writes").path("tuple_keys").get(0).path("condition").path("name").textValue())
                .isEqualTo("in_region");
        assertThat(json.path("deletes").path("on_missing").textValue()).isEqualTo("ignore");
        assertThat(json.path("deletes").path("tuple_keys").get(0).has("condition")).isFalse();
    }

    @Test
    void streamedListObjectsResponseDecodesObject() throws Exception {
        var response = mapper.readValue("{\"object\":\"document:roadmap\"}", StreamedListObjectsResponse.class);

        assertThat(response.object()).isEqualTo(RelObject.of("document", "roadmap"));
    }

    private static RelTupleDefinition conditionalTuple() {
        return RelTupleDefinition.builder()
                .object(RelObject.of("document", "roadmap"))
                .relation("viewer")
                .user(RelUser.of("user", "anne"))
                .condition(RelCondition.of("in_region", Map.of("region", "eu")))
                .build();
    }

    private static JsonNode json(Object value) throws Exception {
        return mapper.readTree(mapper.writeValueAsString(value));
    }
}
