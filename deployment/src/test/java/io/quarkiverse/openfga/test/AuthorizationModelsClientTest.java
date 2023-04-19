package io.quarkiverse.openfga.test;

import static java.time.Duration.ofSeconds;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.AuthorizationModelsClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.model.*;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class AuthorizationModelsClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    OpenFGAClient openFGAClient;

    Store store;
    StoreClient storeClient;
    AuthorizationModelsClient authorizationModelsClient;

    @BeforeEach
    public void createTestStore() {
        store = openFGAClient.createStore("test").await().atMost(ofSeconds(10));
        storeClient = openFGAClient.store(store.getId());
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

        var userTypeDef = new TypeDefinition("user");
        var documentTypeDef = new TypeDefinition("document", Map.of(
                "reader", Userset.direct("a", 1)),
                new Metadata(
                        Map.of("reader", new RelationMetadata(List.of(new RelationReference("user"))))));

        var typeDefinitions = new TypeDefinitions(List.of(userTypeDef, documentTypeDef));

        for (int c = 0; c < 4; c++) {
            authorizationModelsClient.create(typeDefinitions)
                    .subscribe().withSubscriber(UniAssertSubscriber.create())
                    .awaitItem();
        }

        var models = authorizationModelsClient.listAll(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(models, hasSize(4));
    }
}
