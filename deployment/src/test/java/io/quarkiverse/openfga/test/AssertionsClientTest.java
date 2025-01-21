package io.quarkiverse.openfga.test;

import static io.quarkiverse.openfga.test.SchemaFixtures.document123;
import static io.quarkiverse.openfga.test.SchemaFixtures.userMe;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.AssertionsClient;
import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.model.Assertion;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.test.SchemaFixtures.RelationshipNames;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class AssertionsClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(SchemaFixtures.class));

    @Inject
    OpenFGAClient openFGAClient;

    Store store;

    StoreClient storeClient;
    AuthorizationModelClient authorizationModelClient;
    AssertionsClient assertionsClient;

    @BeforeEach
    public void createTestStore() {
        store = openFGAClient.createStore("test").await().atMost(ofSeconds(10));
        storeClient = openFGAClient.store(store.getId());
        var modelId = storeClient.authorizationModels()
                .create(SchemaFixtures.schema)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        authorizationModelClient = storeClient.authorizationModels().model(modelId);
        assertionsClient = authorizationModelClient.assertions();
    }

    @AfterEach
    public void deleteTestStore() {
        if (storeClient != null) {
            storeClient.delete().await().atMost(ofSeconds(10));
        }
    }

    @Test
    @DisplayName("Update List Assertions")
    public void updateListAssertions() {

        var tupleDef1 = document123.define(RelationshipNames.READER, userMe);
        var tupleDef2 = document123.define(RelationshipNames.WRITER, userMe);

        authorizationModelClient.write(tupleDef1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        assertionsClient.update(Assertion.of(tupleDef1, true), Assertion.of(tupleDef2, false))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = assertionsClient.list()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(list)
                .hasSize(2)
                .extracting(Assertion::getExpectation, Assertion::getTupleKey)
                .containsExactlyInAnyOrder(
                        tuple(true, tupleDef1.key()),
                        tuple(false, tupleDef2.key()));
    }
}
