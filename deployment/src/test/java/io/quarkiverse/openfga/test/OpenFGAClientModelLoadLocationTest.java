package io.quarkiverse.openfga.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class OpenFGAClientModelLoadLocationTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("application-with-model-location.properties", "application.properties"));

    @Inject
    OpenFGAClient client;

    @Test
    @DisplayName("Can Find Store Created By Authorization Model Load Location")
    public void canFindLoadedStore() {

        var store = client.listAllStores()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem()
                .stream().filter((s) -> s.getName().equals("store-loaded-by-location"))
                .findFirst().orElse(null);

        assertThat(store, not(equalTo(nullValue())));

        var models = client.store(store.getId())
                .authorizationModels()
                .listAll()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(models, hasSize(1));
    }

}
