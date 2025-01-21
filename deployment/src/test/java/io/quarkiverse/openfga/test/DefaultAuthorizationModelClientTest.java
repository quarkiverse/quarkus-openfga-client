package io.quarkiverse.openfga.test;

import static io.quarkiverse.openfga.test.SchemaFixtures.*;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.AuthorizationModelClient.ListObjectsFilter;
import io.quarkiverse.openfga.client.AuthorizationModelClient.ReadFilter;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.model.RelTuple;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class DefaultAuthorizationModelClientTest {

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

    @BeforeEach
    public void createTestStoreAndModel() {
        store = openFGAClient.createStore("test").await().atMost(ofSeconds(10));
        storeClient = openFGAClient.store(store.getId());

        storeClient.authorizationModels().create(SchemaFixtures.schema)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        authorizationModelClient = storeClient.authorizationModels().defaultModel();
    }

    @AfterEach
    public void deleteTestStore() {
        if (storeClient != null) {
            storeClient.delete().await().atMost(ofSeconds(10));
        }
    }

    @Test
    @DisplayName("Read & Write Tuples")
    public void readWriteTuples() {

        var tuple = document123.define(RelationshipNames.READER, userMe);

        var writes = authorizationModelClient.write(tuple)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes)
                .hasSize(0);

        var list = authorizationModelClient.readAll()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem()
                .stream().map(RelTuple::getKey).collect(Collectors.toList());

        assertThat(list)
                .isEqualTo(List.of(tuple.conditional()));
    }

    @Test
    @DisplayName("Check Successfully")
    public void checkSuccessfully() {

        var tupleDef = document123.define(RelationshipNames.READER, userMe);

        var writes = authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .hasSize(0);

        var allowed = authorizationModelClient.check(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(allowed)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Read Tuples Matching Object")
    public void readTuplesMatchingObject() {

        var readerTuple = document123.define(RelationshipNames.READER, userMe);
        var writerTuple = document123.define(RelationshipNames.WRITER, userMe);

        authorizationModelClient.write(readerTuple, writerTuple)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObject(document123))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .map(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTuple.conditional(), writerTuple.conditional());
    }

    @Test
    @DisplayName("Read Tuples Matching Object and Relation")
    public void readTuplesMatchingObjectRelation() {

        var meTuple = document123.define(RelationshipNames.READER, userMe);
        var youTuple = document123.define(RelationshipNames.READER, userYou);
        var otherTuple = document123.define(RelationshipNames.WRITER, userYou);

        authorizationModelClient.write(meTuple, youTuple, otherTuple)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObject(document123).relation(RelationshipNames.READER))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .map(RelTuple::getKey)
                .containsExactlyInAnyOrder(meTuple.conditional(), youTuple.conditional());
    }

    @Test
    @DisplayName("Read Tuples Matching Object and User")
    public void readTuplesMatchingObjectUser() {

        var readerTuple = document123.define(RelationshipNames.READER, userMe);
        var writerTuple = document123.define(RelationshipNames.WRITER, userMe);
        var otherTuple = document123.define(RelationshipNames.WRITER, userYou);

        authorizationModelClient.write(readerTuple, writerTuple, otherTuple)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObject(document123).user(userMe))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .map(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTuple.conditional(), writerTuple.conditional());
    }

    @Test
    @DisplayName("Read Matching Object Type and User")
    public void readTuplesMatchingObjectTypeUser() {

        var readerTuple = document123.define(RelationshipNames.READER, userMe);
        var writerTuple = document123.define(RelationshipNames.WRITER, userMe);
        var otherTuple = otherDocument123.define(RelationshipNames.READER, userMe);

        authorizationModelClient.write(readerTuple, writerTuple, otherTuple)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObjectType(documentType).user(userMe))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .map(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTuple.conditional(), writerTuple.conditional());
    }

    @Test
    @DisplayName("Read Tuples With Contextual Tuples")
    public void readTuplesWithContextualTuples() {

        var readerTuple = document123.define(RelationshipNames.WRITER, userMe);
        var writerTuple = document123.define(RelationshipNames.READER, userMe);
        var otherTuple = document123.define(RelationshipNames.READER, userYou);

        authorizationModelClient.write(readerTuple, writerTuple, otherTuple)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObject(document123).user(userMe))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .map(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTuple.conditional(), writerTuple.conditional());
    }

    @Test
    @DisplayName("List Objects Matching Object Type, Relation, and User")
    public void listAllRelationshipsForObjectTypeRelationAndUser() {

        var readerTuple = document123.define(RelationshipNames.WRITER, userMe);
        var writerTuple = document456.define(RelationshipNames.WRITER, userMe);
        var other1Tuple = otherDocument123.define(RelationshipNames.WRITER, userMe);
        var other2Tuple = document123.define(RelationshipNames.READER, userMe);
        var other3Tuple = document456.define(RelationshipNames.WRITER, userYou);

        authorizationModelClient.write(readerTuple, writerTuple, other1Tuple, other2Tuple, other3Tuple)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.listObjects(ListObjectsFilter.byObjectType(documentType)
                .relation(RelationshipNames.WRITER).user(userMe))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .containsExactlyInAnyOrder(document123, document456);
    }
}
