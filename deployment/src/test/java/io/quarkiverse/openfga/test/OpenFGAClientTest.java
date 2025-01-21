package io.quarkiverse.openfga.test;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import jakarta.inject.Inject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.OpenFGAClient.ListStoresFilter;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.client.utils.Pagination;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class OpenFGAClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(SchemaFixtures.class));

    @Inject
    API api;

    @Inject
    OpenFGAClient client;

    @BeforeEach
    public void deleteAllStores() {
        for (var store : client.listAllStores().await().indefinitely()) {
            client.store(store.getId()).delete().await().indefinitely();
        }
    }

    @Test
    @DisplayName("Can Create Stores")
    public void canCreateStores() {

        var store = client.createStore("testing")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(store)
                .isNotNull()
                .satisfies(s -> {
                    assertThat(s.getId()).isNotEmpty();
                    assertThat(s.getName()).isEqualTo("testing");
                    assertThat(s.getCreatedAt()).isCloseTo(now(), within(5, SECONDS));
                    assertThat(s.getUpdatedAt()).isCloseTo(now(), within(5, SECONDS));
                    assertThat(s.getDeletedAt()).isNull();
                });
    }

    @Test
    @DisplayName("Can List Stores")
    public void canListStores() {

        var createdStores = Multi.createFrom().items("testing1", "testing2")
                .onItem().transformToUniAndConcatenate(name -> client.createStore(name))
                .collect().asList()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(createdStores)
                .isNotNull()
                .map(Store::getName)
                .containsExactlyInAnyOrder("testing1", "testing2");

        var list = client.listStores()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .isNotNull()
                .extracting(PaginatedList::getItems, as(InstanceOfAssertFactories.collection(Store.class)))
                .containsExactlyInAnyOrderElementsOf(createdStores);
    }

    @Test
    @DisplayName("Can List Stores With Name")
    @Disabled("Using the name filter causes a server error")
    public void canListStoresWithName() {

        var createdStores = Multi.createFrom().items("testing")
                .onItem().transformToUniAndConcatenate(name -> client.createStore(name))
                .collect().asList()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(createdStores)
                .isNotNull()
                .map(Store::getName)
                .containsExactlyInAnyOrder("testing");

        var list = client.listStores(ListStoresFilter.named("testing"))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .isNotNull()
                .extracting(PaginatedList::getItems, as(InstanceOfAssertFactories.collection(Store.class)))
                .containsExactlyInAnyOrderElementsOf(createdStores);
    }

    @Test
    @DisplayName("Can List Stores With Pagination")
    public void canListStoresWithPagination() {

        var createdStores = Multi.createFrom().items("testing1", "testing2", "testing3", "testing4", "testing5")
                .onItem().transformToUniAndConcatenate(name -> client.createStore(name))
                .collect().asList()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(createdStores)
                .isNotNull()
                .map(Store::getName)
                .containsExactlyInAnyOrder("testing1", "testing2", "testing3", "testing4", "testing5");

        var page1 = client.listStores(Pagination.limitedTo(2))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(page1)
                .isNotNull()
                .extracting(PaginatedList::getItems, as(InstanceOfAssertFactories.collection(Store.class)))
                .hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("testing1", "testing2");

        var page2 = client.listStores(Pagination.limitedTo(2).andContinuingFrom(page1.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(page2)
                .isNotNull()
                .extracting(PaginatedList::getItems, as(InstanceOfAssertFactories.collection(Store.class)))
                .hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("testing3", "testing4");

        var page3 = client.listStores(Pagination.limitedTo(2).andContinuingFrom(page2.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(page3)
                .isNotNull()
                .extracting(PaginatedList::getItems, as(InstanceOfAssertFactories.collection(Store.class)))
                .hasSize(1)
                .extracting("name")
                .containsExactlyInAnyOrder("testing5");
    }

    @Test
    @DisplayName("Can List All Stores (Auto-Pagination)")
    public void canListAllStores() {

        var createdStores = Multi.createFrom().items("testing1", "testing2", "testing3")
                .onItem().transformToUniAndConcatenate(name -> client.createStore(name))
                .collect().asList()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(createdStores)
                .isNotNull()
                .map(Store::getName)
                .containsExactlyInAnyOrder("testing1", "testing2", "testing3");

        var list = client.listAllStores(ListStoresFilter.ALL)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(createdStores);
    }

    @Test
    @DisplayName("Can Delete Stores")
    public void canDeleteStores() {

        var store = client.createStore("testing")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        var preList = client.listAllStores()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(preList)
                .isNotNull()
                .hasSize(1);

        client.store(store.getId()).delete()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted();

        var postList = client.listAllStores(ListStoresFilter.ALL, 1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(postList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void testHealthViaAPI() {

        var healthzResponse = api.health()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(healthzResponse.status())
                .isEqualTo("SERVING");
    }

}
