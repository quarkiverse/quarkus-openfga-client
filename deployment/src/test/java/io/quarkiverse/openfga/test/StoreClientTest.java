package io.quarkiverse.openfga.test;

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
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class StoreClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
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

    private AuthorizationModelClient initializeAuthorizationModel() throws Exception {

        try (var schemaStream = unitTest.getArchiveProducer().get().get("/auth-model.json").getAsset().openStream()) {
            if (schemaStream == null) {
                throw new IllegalStateException("Could not find auth-model.json");
            }

            var schema = AuthorizationModelSchema.parse(schemaStream);

            var authorizationModelId = storeClient.authorizationModels()
                    .create(schema)
                    .subscribe().withSubscriber(UniAssertSubscriber.create())
                    .awaitItem()
                    .getItem();

            return storeClient.authorizationModels().model(authorizationModelId);
        }
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
    public void canReadChanges() throws Exception {
        var authorizationModelClient = initializeAuthorizationModel();
        authorizationModelClient.write(
                List.of(
                        ConditionalTupleKey.of("thing:1", "owner", "user:me"),
                        ConditionalTupleKey.of("thing:2", "owner", "user:you"),
                        ConditionalTupleKey.of("thing:3", "reader", "user:me")),
                List.of()).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        authorizationModelClient.write(
                List.of(),
                List.of(TupleKey.of("thing:3", "reader", "user:me"))).subscribe()
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
                        tuple(ConditionalTupleKey.of("thing:1", "owner", "user:me"), TupleOperation.WRITE),
                        tuple(ConditionalTupleKey.of("thing:2", "owner", "user:you"), TupleOperation.WRITE),
                        tuple(ConditionalTupleKey.of("thing:3", "reader", "user:me"), TupleOperation.WRITE),
                        tuple(ConditionalTupleKey.of("thing:3", "reader", "user:me"), TupleOperation.DELETE));
    }

    @Test
    @DisplayName("Can Read Changes With Pagination")
    public void canReadChangesWithPagination() throws Exception {
        var authorizationModelClient = initializeAuthorizationModel();
        authorizationModelClient.write(
                List.of(
                        ConditionalTupleKey.of("thing:1", "owner", "user:me"),
                        ConditionalTupleKey.of("thing:1", "reader", "user:you"),
                        ConditionalTupleKey.of("thing:2", "owner", "user:you"),
                        ConditionalTupleKey.of("thing:2", "reader", "user:me"),
                        ConditionalTupleKey.of("thing:3", "owner", "user:me"),
                        ConditionalTupleKey.of("thing:3", "reader", "user:you")),
                List.of()).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var page1 = storeClient.readChanges(Pagination.limitedTo(2))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(page1.getItems())
                .hasSize(2)
                .extracting("tupleKey.object", "tupleKey.relation", "tupleKey.user")
                .containsExactly(
                        tuple("thing:1", "owner", "user:me"),
                        tuple("thing:1", "reader", "user:you"));
        assertThat(page1.getToken()).isNotNull();

        var page2 = storeClient.readChanges(Pagination.limitedTo(2).andContinuingFrom(page1.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(page2.getItems())
                .hasSize(2)
                .extracting("tupleKey.object", "tupleKey.relation", "tupleKey.user")
                .containsExactly(
                        tuple("thing:2", "owner", "user:you"),
                        tuple("thing:2", "reader", "user:me"));
        assertThat(page2.getToken()).isNotNull();

        var page3 = storeClient.readChanges(Pagination.limitedTo(2).andContinuingFrom(page2.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(page3.getItems())
                .hasSize(2)
                .extracting("tupleKey.object", "tupleKey.relation", "tupleKey.user")
                .containsExactly(
                        tuple("thing:3", "owner", "user:me"),
                        tuple("thing:3", "reader", "user:you"));
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
    public void canReadChangesWithFilter() throws Exception {
        var authorizationModelClient = initializeAuthorizationModel();
        authorizationModelClient.write(
                List.of(
                        ConditionalTupleKey.of("thing:1", "owner", "user:me"),
                        ConditionalTupleKey.of("thing:2", "owner", "user:you"),
                        ConditionalTupleKey.of("thing:3", "reader", "user:me"),
                        ConditionalTupleKey.of("other-thing:1", "owner", "user:me"),
                        ConditionalTupleKey.of("other-thing:2", "reader", "group:us")),
                List.of()).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        authorizationModelClient.write(
                List.of(),
                List.of(TupleKey.of("thing:3", "reader", "user:me"))).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var thingChanges = storeClient.readChanges(ReadChangesFilter.only("thing"))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(thingChanges.getItems())
                .hasSize(4)
                .extracting("tupleKey.object", "operation")
                .containsExactly(
                        tuple("thing:1", TupleOperation.WRITE),
                        tuple("thing:2", TupleOperation.WRITE),
                        tuple("thing:3", TupleOperation.WRITE),
                        tuple("thing:3", TupleOperation.DELETE));

        var otherThingChanges = storeClient.readChanges(ReadChangesFilter.only("other-thing"))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(otherThingChanges.getItems())
                .hasSize(2)
                .extracting("tupleKey.object", "operation")
                .containsExactly(
                        tuple("other-thing:1", TupleOperation.WRITE),
                        tuple("other-thing:2", TupleOperation.WRITE));
    }

    @Test
    @DisplayName("Can Read Changes After Start Time")
    public void canReadChangesAfterStart() throws Exception {
        var authorizationModelClient = initializeAuthorizationModel();
        authorizationModelClient.write(
                List.of(
                        ConditionalTupleKey.of("thing:1", "owner", "user:me"),
                        ConditionalTupleKey.of("thing:1", "reader", "user:you"),
                        ConditionalTupleKey.of("thing:2", "owner", "user:you")),
                List.of()).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        Thread.sleep(1000);

        var time = OffsetDateTime.now();

        authorizationModelClient.write(
                List.of(
                        ConditionalTupleKey.of("thing:2", "reader", "user:me"),
                        ConditionalTupleKey.of("thing:3", "owner", "user:me"),
                        ConditionalTupleKey.of("thing:3", "reader", "user:you")),
                List.of(TupleKey.of("thing:1", "reader", "user:you"))).subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var thingChanges = storeClient.readChanges(ReadChangesFilter.since(time))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(thingChanges.getItems())
                .hasSize(4)
                .extracting("tupleKey.object", "operation")
                .containsExactly(
                        tuple("thing:1", TupleOperation.DELETE),
                        tuple("thing:2", TupleOperation.WRITE),
                        tuple("thing:3", TupleOperation.WRITE),
                        tuple("thing:3", TupleOperation.WRITE));
    }
}
