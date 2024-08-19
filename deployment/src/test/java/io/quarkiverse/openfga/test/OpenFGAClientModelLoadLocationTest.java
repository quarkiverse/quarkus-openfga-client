package io.quarkiverse.openfga.test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import jakarta.inject.Inject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.model.AuthorizationModel;
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
                .findFirst();

        assertThat(store)
                .isNotNull()
                .map(s -> client.store(s.getId())
                        .authorizationModels()
                        .listAll()
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create())
                        .awaitItem()
                        .getItem())
                .get(InstanceOfAssertFactories.collection(AuthorizationModel.class))
                .hasSize(1);

    }

}
