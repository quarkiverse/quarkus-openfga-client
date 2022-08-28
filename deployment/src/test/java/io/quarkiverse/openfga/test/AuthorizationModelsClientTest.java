package io.quarkiverse.openfga.test;

import static java.time.Duration.ofSeconds;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.AuthorizationModelsClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.StoresClient;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.TypeDefinition;
import io.quarkiverse.openfga.client.model.Userset;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class AuthorizationModelsClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    StoresClient storesClient;

    Store store;
    StoreClient storeClient;
    AuthorizationModelsClient authorizationModelsClient;

    @BeforeEach
    public void createTestStore() {
        store = storesClient.create("test").await().atMost(ofSeconds(10));
        storeClient = storesClient.store(store.getId());
        authorizationModelsClient = storeClient.authorizationModels();
    }

    @AfterEach
    public void deleteTestStore() {
        if (storeClient != null) {
            storeClient.delete().await().atMost(ofSeconds(10));
        }
    }

    @Test
    @DisplayName("Can List Models")
    public void canList() {

        var typeDefinition = new TypeDefinition("document", Map.of("reader", Userset.direct()));

        authorizationModelsClient.create(List.of(typeDefinition))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var models = authorizationModelsClient.listAll()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(models, hasSize(1));
    }
}
