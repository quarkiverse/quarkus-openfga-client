package io.quarkiverse.openfga.test;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.exparity.hamcrest.date.OffsetDateTimeMatchers.within;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.StoresClient;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class StoresClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    StoresClient storesClient;

    @BeforeEach
    public void deleteAllStores() {
        for (var store : storesClient.listAll().await().indefinitely()) {
            storesClient.store(store.getId()).delete().await().indefinitely();
        }
    }

    @Test
    @DisplayName("Can Create Stores")
    public void canCreateStores() {

        var store = storesClient.create("testing")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(store.getId(), not(emptyOrNullString()));
        assertThat(store.getName(), equalTo("testing"));
        assertThat(store.getCreatedAt(), within(5, SECONDS, now()));
        assertThat(store.getUpdatedAt(), within(5, SECONDS, now()));
        assertThat(store.getDeletedAt(), is(nullValue()));
    }

    @Test
    @DisplayName("Can List Stores Without Pagination")
    public void canListStoresWithoutPagination() {

        var createdStores = Multi.createFrom().items("testing1", "testing2")
                .onItem().transformToUniAndConcatenate(name -> storesClient.create(name))
                .collect().asList()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(createdStores.stream().map(Store::getName).collect(Collectors.toList()),
                containsInAnyOrder("testing1", "testing2"));

        var list = storesClient.list(2, null)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list.getItems(), containsInAnyOrder(createdStores.toArray()));
    }

    @Test
    @DisplayName("Can List Stores With Pagination")
    public void canListStoresWithPagination() {

        var createdStores = Multi.createFrom().items("testing1", "testing2", "testing3")
                .onItem().transformToUniAndConcatenate(name -> storesClient.create(name))
                .collect().asList()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(createdStores.stream().map(Store::getName).collect(Collectors.toList()),
                containsInAnyOrder("testing1", "testing2", "testing3"));

        var list = storesClient.listAll(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list, containsInAnyOrder(createdStores.toArray()));
    }

    @Test
    @DisplayName("Can Delete Stores")
    public void canDeleteStores() {

        var store = storesClient.create("testing")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        var preList = storesClient.listAll(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(preList, hasSize(1));

        storesClient.store(store.getId()).delete()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted();

        var postList = storesClient.listAll(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(postList, hasSize(0));
    }

}
