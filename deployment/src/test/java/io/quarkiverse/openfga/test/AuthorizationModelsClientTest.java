package io.quarkiverse.openfga.test;

import static io.quarkiverse.openfga.client.model.AuthorizationModelSchema.DEFAULT_SCHEMA_VERSION;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.AuthorizationModelsClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.model.AuthorizationModel;
import io.quarkiverse.openfga.client.model.Schema.Condition;
import io.quarkiverse.openfga.client.model.Schema.TypeDefinition;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.client.utils.Pagination;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class AuthorizationModelsClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(SchemaFixtures.class));

    @Inject
    OpenFGAClient openFGAClient;

    Store store;

    StoreClient storeClient;
    AuthorizationModelsClient authorizationModelsClient;

    @BeforeEach
    public void createTestStore() {
        store = openFGAClient.createStore("test").await().atMost(ofSeconds(10));
        storeClient = openFGAClient.store(store.getId());
        authorizationModelsClient = storeClient.authorizationModels();
    }

    @AfterEach
    public void deleteTestStore() {
        if (storeClient != null) {
            storeClient.delete().await().atMost(ofSeconds(10));
        }
    }

    private List<String> createTestModels(int count) {

        var ids = new ArrayList<String>();
        for (int c = 0; c < count; c++) {
            var modelId = authorizationModelsClient.create(SchemaFixtures.schema)
                    .subscribe().withSubscriber(UniAssertSubscriber.create())
                    .awaitItem()
                    .getItem();
            ids.add(modelId);
        }

        return ids;
    }

    @Test
    @DisplayName("Can List Models")
    public void canList() {

        var modelIds = createTestModels(4);

        var list = authorizationModelsClient.list()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(AuthorizationModel.class)))
                .hasSize(4)
                .allSatisfy(model -> {
                    assertThat(model)
                            .extracting(AuthorizationModel::getId, as(STRING))
                            .isIn(modelIds);
                    assertThat(model)
                            .extracting(AuthorizationModel::getSchemaVersion, as(STRING))
                            .isEqualTo(DEFAULT_SCHEMA_VERSION);
                    assertThat(model)
                            .extracting(AuthorizationModel::getTypeDefinitions, as(iterable(TypeDefinition.class)))
                            .containsExactlyInAnyOrderElementsOf(SchemaFixtures.schema.getTypeDefinitions());
                    assertThat(model)
                            .extracting(AuthorizationModel::getConditions)
                            .isNull();
                });
    }

    @Test
    @DisplayName("Can List Models With Pagination")
    public void canListWithPagination() {

        var modelIds = createTestModels(5);

        var page1 = authorizationModelsClient.list(Pagination.limitedTo(2))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(page1)
                .extracting(PaginatedList::getItems, as(iterable(AuthorizationModel.class)))
                .hasSize(2)
                .allSatisfy(model -> {
                    assertThat(model)
                            .extracting(AuthorizationModel::getId, as(STRING))
                            .isIn(modelIds);
                    assertThat(model)
                            .extracting(AuthorizationModel::getSchemaVersion)
                            .isEqualTo(DEFAULT_SCHEMA_VERSION);
                    assertThat(model)
                            .extracting(AuthorizationModel::getTypeDefinitions, as(iterable(TypeDefinition.class)))
                            .containsExactlyInAnyOrderElementsOf(SchemaFixtures.schema.getTypeDefinitions());
                    assertThat(model)
                            .extracting(AuthorizationModel::getConditions)
                            .isNull();
                });
        assertThat(page1)
                .extracting(PaginatedList::getToken, as(STRING))
                .isNotBlank();

        var page2 = authorizationModelsClient.list(Pagination.limitedTo(2).andContinuingFrom(page1.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(page2)
                .extracting(PaginatedList::getItems, as(iterable(AuthorizationModel.class)))
                .hasSize(2)
                .allSatisfy(model -> {
                    assertThat(model)
                            .extracting(AuthorizationModel::getId, as(STRING))
                            .isIn(modelIds);
                    assertThat(model)
                            .extracting(AuthorizationModel::getSchemaVersion)
                            .isEqualTo(DEFAULT_SCHEMA_VERSION);
                    assertThat(model)
                            .extracting(AuthorizationModel::getTypeDefinitions, as(iterable(TypeDefinition.class)))
                            .containsExactlyInAnyOrderElementsOf(SchemaFixtures.schema.getTypeDefinitions());
                    assertThat(model)
                            .extracting(AuthorizationModel::getConditions)
                            .isNull();
                });
        assertThat(page2)
                .extracting(PaginatedList::getToken, as(STRING))
                .isNotBlank();

        var page3 = authorizationModelsClient.list(Pagination.limitedTo(2).andContinuingFrom(page2.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(page3)
                .extracting(PaginatedList::getItems, as(iterable(AuthorizationModel.class)))
                .hasSize(1)
                .allSatisfy(model -> {
                    assertThat(model)
                            .extracting(AuthorizationModel::getId, as(STRING))
                            .isIn(modelIds);
                    assertThat(model)
                            .extracting(AuthorizationModel::getSchemaVersion)
                            .isEqualTo(DEFAULT_SCHEMA_VERSION);
                    assertThat(model)
                            .extracting(AuthorizationModel::getTypeDefinitions, as(iterable(TypeDefinition.class)))
                            .containsExactlyInAnyOrderElementsOf(SchemaFixtures.schema.getTypeDefinitions());
                    assertThat(model)
                            .extracting(AuthorizationModel::getConditions)
                            .isNull();
                });
        assertThat(page3)
                .extracting(PaginatedList::getToken, as(STRING))
                .isEmpty();
    }

    @Test
    @DisplayName("Can List All Models")
    public void canListAll() {

        var modelIds = createTestModels(5);

        var list = authorizationModelsClient.listAll(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .hasSize(5)
                .extracting(AuthorizationModel::getId)
                .containsExactlyInAnyOrderElementsOf(modelIds);
    }

    @Test
    @DisplayName("Can Create Model With Schema")
    public void canCreateModelWithSchema() {

        var modelId = authorizationModelsClient.create(SchemaFixtures.schema)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        var models = authorizationModelsClient.listAll()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(models)
                .hasSize(1)
                .allSatisfy(model -> {
                    assertThat(model)
                            .extracting(AuthorizationModel::getId, as(STRING))
                            .isEqualTo(modelId);
                    assertThat(model)
                            .extracting(AuthorizationModel::getSchemaVersion)
                            .isEqualTo(DEFAULT_SCHEMA_VERSION);
                    assertThat(model)
                            .extracting(AuthorizationModel::getTypeDefinitions, as(iterable(TypeDefinition.class)))
                            .containsExactlyInAnyOrderElementsOf(SchemaFixtures.schema.getTypeDefinitions());
                    assertThat(model)
                            .extracting(AuthorizationModel::getConditions)
                            .isNull();
                });
    }

    @Test
    @DisplayName("Can Create Model With Properties")
    public void canCreateModelWithProperties() {

        var modelId = authorizationModelsClient
                .create(DEFAULT_SCHEMA_VERSION, SchemaFixtures.schema.getTypeDefinitions(), null)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        var models = authorizationModelsClient.listAll()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(models)
                .hasSize(1)
                .allSatisfy(model -> {
                    assertThat(model)
                            .extracting(AuthorizationModel::getId, as(STRING))
                            .isEqualTo(modelId);
                    assertThat(model)
                            .extracting(AuthorizationModel::getSchemaVersion)
                            .isEqualTo(DEFAULT_SCHEMA_VERSION);
                    assertThat(model)
                            .extracting(AuthorizationModel::getTypeDefinitions, as(iterable(TypeDefinition.class)))
                            .containsExactlyInAnyOrderElementsOf(SchemaFixtures.schema.getTypeDefinitions());
                    assertThat(model)
                            .extracting(AuthorizationModel::getConditions)
                            .isNull();
                });
    }

    @Test
    @DisplayName("Can Create Model With Conditions")
    public void canCreateModelWithConditions() {

        var modelId = authorizationModelsClient.create(SchemaFixtures.schemaWithCondition)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        var models = authorizationModelsClient.listAll()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(models)
                .hasSize(1)
                .allSatisfy(model -> {
                    assertThat(model)
                            .extracting(AuthorizationModel::getId, as(STRING))
                            .isEqualTo(modelId);
                    assertThat(model)
                            .extracting(AuthorizationModel::getSchemaVersion)
                            .isEqualTo(DEFAULT_SCHEMA_VERSION);
                    assertThat(model)
                            .extracting(AuthorizationModel::getTypeDefinitions, as(iterable(TypeDefinition.class)))
                            .containsExactlyInAnyOrderElementsOf(SchemaFixtures.schemaWithCondition.getTypeDefinitions());
                    assertThat(model)
                            .extracting(AuthorizationModel::getConditions, as(map(String.class, Condition.class)))
                            .usingRecursiveComparison()
                            .isEqualTo(SchemaFixtures.schemaWithCondition.getConditions());
                });
    }
}
