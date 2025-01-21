package io.quarkiverse.openfga.test;

import static io.quarkiverse.openfga.test.SchemaFixtures.*;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.StoreClient.ReadChangesFilter;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.utils.Pagination;
import io.quarkiverse.openfga.test.SchemaFixtures.RelationshipNames;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class StoreClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(SchemaFixtures.class)
                    .addAsResource("auth-model.json"));

    @Inject
    OpenFGAClient openFGAClient;

    Store store;

    StoreClient storeClient;

    @BeforeEach
    public void createTestStore() {
        store = openFGAClient.createStore("test").await().atMost(ofSeconds(10));
        storeClient = openFGAClient.store(store.getId());
    }

    @AfterEach
    public void deleteTestStore() {
        if (storeClient != null) {
            storeClient.delete().await().atMost(ofSeconds(10));
        }
    }

    private AuthorizationModelClient initializeAuthorizationModel() {

        var authorizationModelId = storeClient.authorizationModels()
                .create(SchemaFixtures.schema)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        return storeClient.authorizationModels().model(authorizationModelId);
    }

    @Test
    @DisplayName("Can Get Store")
    public void canGetStore() {

        var foundStore = storeClient.get()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(foundStore)
                .isEqualTo(store);
    }

    @Test
    @DisplayName("Can Delete Store")
    public void canDeleteStores() {

        var preList = openFGAClient.listAllStores()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(preList)
                .contains(store);

        storeClient.delete()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted();

        var postList = openFGAClient.listAllStores()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(postList)
                .doesNotContain(store);
    }

    @Test
    @DisplayName("Can Read Changes")
    public void canReadChanges() {
        var authorizationModelClient = initializeAuthorizationModel();

        var docTuple1 = document123.define(RelationshipNames.READER, userMe);
        var docTuple2 = document456.define(RelationshipNames.READER, userYou);
        var docTuple3 = document789.define(RelationshipNames.OWNER, userMe);

        authorizationModelClient.write(
                List.of(docTuple1, docTuple2, docTuple3),
                List.of()).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        authorizationModelClient.write(
                List.of(),
                List.of(docTuple3)).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var changes = storeClient.readChanges()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(changes.getItems())
                .hasSize(4)
                .extracting("tupleKey", "operation")
                .containsExactlyInAnyOrder(
                        tuple(docTuple1.conditional(), TupleOperation.WRITE),
                        tuple(docTuple2.conditional(), TupleOperation.WRITE),
                        tuple(docTuple3.conditional(), TupleOperation.WRITE),
                        tuple(docTuple3.conditional(), TupleOperation.DELETE));
    }

    @Test
    @DisplayName("Can Read Changes With Pagination")
    public void canReadChangesWithPagination() {
        var authorizationModelClient = initializeAuthorizationModel();

        var docTuple11 = document123.define(RelationshipNames.OWNER, userMe);
        var docTuple12 = document123.define(RelationshipNames.READER, userYou);
        var docTuple21 = document456.define(RelationshipNames.OWNER, userYou);
        var docTuple22 = document456.define(RelationshipNames.READER, userMe);
        var docTuple31 = document789.define(RelationshipNames.OWNER, userMe);
        var docTuple32 = document789.define(RelationshipNames.READER, userYou);

        authorizationModelClient.write(
                List.of(docTuple11, docTuple12, docTuple21, docTuple22, docTuple31, docTuple32),
                List.of()).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var page1 = storeClient.readChanges(Pagination.limitedTo(2))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(page1.getItems())
                .hasSize(2)
                .extracting(RelTupleChange::getTupleKey)
                .containsExactly(docTuple11.conditional(), docTuple12.conditional());
        assertThat(page1.getToken()).isNotNull();

        var page2 = storeClient.readChanges(Pagination.limitedTo(2).andContinuingFrom(page1.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(page2.getItems())
                .hasSize(2)
                .extracting(RelTupleChange::getTupleKey)
                .containsExactly(docTuple21.conditional(), docTuple22.conditional());
        assertThat(page2.getToken()).isNotNull();

        var page3 = storeClient.readChanges(Pagination.limitedTo(2).andContinuingFrom(page2.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(page3.getItems())
                .hasSize(2)
                .extracting(RelTupleChange::getTupleKey)
                .containsExactly(docTuple31.conditional(), docTuple32.conditional());
        assertThat(page3.getToken()).isNotNull();

        var page4 = storeClient.readChanges(Pagination.limitedTo(2).andContinuingFrom(page3.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(page4.getItems()).isEmpty();
        assertThat(page4.getToken()).isNotNull();
    }

    @Test
    @DisplayName("Can Read Changes with Filter")
    public void canReadChangesWithFilter() {
        var authorizationModelClient = initializeAuthorizationModel();

        var docTuple1 = document123.define(RelationshipNames.OWNER, userMe);
        var docTuple2 = document456.define(RelationshipNames.OWNER, userYou);
        var docTuple3 = document789.define(RelationshipNames.READER, userMe);
        var otherDocTuple1 = otherDocument123.define(RelationshipNames.OWNER, userMe);
        var otherDocTuple2 = otherDocument456.define(RelationshipNames.READER, groupUs);

        authorizationModelClient.write(docTuple1, docTuple2, docTuple3, otherDocTuple1, otherDocTuple2)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        authorizationModelClient.delete(docTuple3)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var docChanges = storeClient.readChanges(ReadChangesFilter.only(ObjectTypeNames.DOCUMENT))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(docChanges.getItems())
                .hasSize(4)
                .extracting(t -> tuple(t.getTupleKey(), t.getOperation()))
                .containsExactly(
                        tuple(docTuple1.conditional(), TupleOperation.WRITE),
                        tuple(docTuple2.conditional(), TupleOperation.WRITE),
                        tuple(docTuple3.conditional(), TupleOperation.WRITE),
                        tuple(docTuple3.conditional(), TupleOperation.DELETE));

        var otherDocChange = storeClient.readChanges(ReadChangesFilter.only(otherDocumentType))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(otherDocChange.getItems())
                .hasSize(2)
                .extracting(t -> tuple(t.getTupleKey(), t.getOperation()))
                .containsExactly(
                        tuple(otherDocTuple1.conditional(), TupleOperation.WRITE),
                        tuple(otherDocTuple2.conditional(), TupleOperation.WRITE));
    }

    @Test
    @DisplayName("Can Read Changes After Start Time")
    public void canReadChangesAfterStart() throws Exception {
        var authorizationModelClient = initializeAuthorizationModel();

        var docTuple1 = document123.define(RelationshipNames.OWNER, userMe);
        var docTuple2 = document123.define(RelationshipNames.READER, userYou);
        var docTuple3 = document456.define(RelationshipNames.OWNER, userYou);
        var docTuple4 = document456.define(RelationshipNames.READER, userMe);
        var docTuple5 = document789.define(RelationshipNames.OWNER, userMe);
        var docTuple6 = document789.define(RelationshipNames.READER, userYou);

        authorizationModelClient.write(docTuple1, docTuple2, docTuple3)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        Thread.sleep(1000);

        var time = OffsetDateTime.now();

        authorizationModelClient.write(
                List.of(docTuple4, docTuple5, docTuple6),
                List.of(docTuple2))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var thingChanges = storeClient.readChanges(ReadChangesFilter.since(time))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(thingChanges.getItems())
                .hasSize(4)
                .extracting(t -> tuple(t.getTupleKey(), t.getOperation()))
                .containsExactly(
                        tuple(docTuple2.conditional(), TupleOperation.DELETE),
                        tuple(docTuple4.conditional(), TupleOperation.WRITE),
                        tuple(docTuple5.conditional(), TupleOperation.WRITE),
                        tuple(docTuple6.conditional(), TupleOperation.WRITE));
    }

    @Test
    @DisplayName("Can Read All Changes")
    public void canReadAllChanges() {

        var authorizationModelClient = initializeAuthorizationModel();

        var docTuple1 = document123.define(RelationshipNames.OWNER, userMe);
        var docTuple2 = document123.define(RelationshipNames.READER, userYou);
        var docTuple3 = document456.define(RelationshipNames.OWNER, userYou);
        var docTuple4 = document456.define(RelationshipNames.READER, userMe);
        var docTuple5 = document789.define(RelationshipNames.OWNER, userMe);
        var docTuple6 = document789.define(RelationshipNames.READER, userYou);

        authorizationModelClient.write(docTuple1, docTuple2, docTuple3, docTuple4, docTuple5, docTuple6)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        authorizationModelClient.delete(docTuple2, docTuple4, docTuple6)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = storeClient.readAllChanges(ReadChangesFilter.only(documentType), 1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(list)
                .hasSize(9)
                .extracting(t -> tuple(t.getTupleKey(), t.getOperation()))
                .containsExactly(
                        tuple(docTuple1.conditional(), TupleOperation.WRITE),
                        tuple(docTuple2.conditional(), TupleOperation.WRITE),
                        tuple(docTuple3.conditional(), TupleOperation.WRITE),
                        tuple(docTuple4.conditional(), TupleOperation.WRITE),
                        tuple(docTuple5.conditional(), TupleOperation.WRITE),
                        tuple(docTuple6.conditional(), TupleOperation.WRITE),
                        tuple(docTuple2.conditional(), TupleOperation.DELETE),
                        tuple(docTuple4.conditional(), TupleOperation.DELETE),
                        tuple(docTuple6.conditional(), TupleOperation.DELETE));

        var list2 = storeClient.readAllChanges(ReadChangesFilter.only(documentType))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(list2)
                .hasSize(9);
    }
}
