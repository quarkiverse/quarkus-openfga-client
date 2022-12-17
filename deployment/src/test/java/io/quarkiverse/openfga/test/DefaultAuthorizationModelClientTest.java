package io.quarkiverse.openfga.test;

import static java.time.Duration.ofSeconds;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static org.exparity.hamcrest.date.OffsetDateTimeMatchers.within;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
import io.quarkiverse.openfga.client.model.*;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class DefaultAuthorizationModelClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    OpenFGAClient openFGAClient;

    Store store;
    StoreClient storeClient;

    AuthorizationModelClient authorizationModelClient;

    @BeforeEach
    public void createTestStoreAndModel() {
        store = openFGAClient.createStore("test").await().atMost(ofSeconds(10));
        storeClient = openFGAClient.store(store.getId());

        // ensure it has an auth model
        var documentTypeDef = new TypeDefinition("document", Map.of(
                "reader", Userset.direct("a", 1),
                "writer", Userset.direct("b", 2)));

        var authModelId = storeClient.authorizationModels().create(List.of(documentTypeDef))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        authorizationModelClient = storeClient.authorizationModels().defaultModel();
    }

    @AfterEach
    public void deleteTestStore() {
        if (storeClient != null) {
            storeClient.delete().await().atMost(ofSeconds(10));
        }
    }

    @Test
    @DisplayName("Can Read & Write Tuples")
    public void canReadWriteTuples() {

        var tuples = List.of(
                TupleKey.of("document:123", "reader", "me"));
        var writes = authorizationModelClient.write(tuples, emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet(), hasSize(0));

        var foundTuples = storeClient.readAllTuples()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem()
                .stream().map(Tuple::getKey).collect(Collectors.toList());

        assertThat(foundTuples, equalTo(tuples));
    }

    @Test
    @DisplayName("Can Execute Checks")
    public void canExecuteChecks() {

        var tuple = TupleKey.of("document:123", "reader", "me");

        var writes = authorizationModelClient.write(List.of(tuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet(), hasSize(0));

        var allowed = authorizationModelClient.check(tuple, null)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(allowed, equalTo(true));
    }

    @Test
    @DisplayName("Can Read All Relationships for an Object")
    public void canReadAllRelationshipsForObject() {

        var readerTuple = TupleKey.of("document:123", "reader", "me");
        var writerTuple = TupleKey.of("document:123", "writer", "me");

        authorizationModelClient.write(List.of(readerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(writerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.queryAllTuples(PartialTupleKey.of("document:123", null, null))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects, containsInAnyOrder(
                allOf(
                        hasProperty("key", equalTo(readerTuple)),
                        hasProperty("timestamp", within(5, SECONDS, now()))),
                allOf(
                        hasProperty("key", equalTo(writerTuple)),
                        hasProperty("timestamp", within(5, SECONDS, now())))));
    }

    @Test
    @DisplayName("Can Read All Relationships for an Object, and Relation")
    public void canReadAllRelationshipsForObjectAndRelation() {

        var meTuple = TupleKey.of("document:123", "reader", "me");
        var youTuple = TupleKey.of("document:123", "reader", "you");

        authorizationModelClient.write(List.of(meTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(youTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.queryAllTuples(PartialTupleKey.of("document:123", "reader", null))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects, containsInAnyOrder(
                allOf(
                        hasProperty("key", equalTo(meTuple)),
                        hasProperty("timestamp", within(5, SECONDS, now()))),
                allOf(
                        hasProperty("key", equalTo(youTuple)),
                        hasProperty("timestamp", within(5, SECONDS, now())))));
    }

    @Test
    @DisplayName("Can Read All Relationships for an Object, and User")
    public void canReadAllRelationshipsForObjectAndUser() {

        var readerTuple = TupleKey.of("document:123", "reader", "me");
        var writerTuple = TupleKey.of("document:123", "writer", "me");

        authorizationModelClient.write(List.of(readerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(writerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.queryAllTuples(PartialTupleKey.of("document:123", null, "me"))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects, containsInAnyOrder(
                allOf(
                        hasProperty("key", equalTo(readerTuple)),
                        hasProperty("timestamp", within(5, SECONDS, now()))),
                allOf(
                        hasProperty("key", equalTo(writerTuple)),
                        hasProperty("timestamp", within(5, SECONDS, now())))));
    }

    @Test
    @DisplayName("Can Read All Relationships for an Object Type, and User")
    public void canReadAllRelationshipsForObjectTypeAndUser() {

        var readerTuple = TupleKey.of("document:123", "reader", "me");
        var writerTuple = TupleKey.of("document:123", "writer", "me");

        authorizationModelClient.write(List.of(readerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(writerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(TupleKey.of("document:123", "writer", "you")), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.queryAllTuples(PartialTupleKey.of("document:", null, "me"))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects, containsInAnyOrder(
                allOf(
                        hasProperty("key", equalTo(readerTuple)),
                        hasProperty("timestamp", within(5, SECONDS, now()))),
                allOf(
                        hasProperty("key", equalTo(writerTuple)),
                        hasProperty("timestamp", within(5, SECONDS, now())))));
    }

    @Test
    @DisplayName("Can List All Objects for an Object Type, Relation, and User")
    public void canListAllRelationshipsForObjectTypeRelationAndUser() {

        var aTuple = TupleKey.of("document:123", "writer", "me");
        var bTuple = TupleKey.of("document:456", "writer", "me");

        authorizationModelClient.write(List.of(aTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(bTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.listObjects("document", "writer", "me", null)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects, containsInAnyOrder("document:123", "document:456"));
    }
}
