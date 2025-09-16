package io.quarkiverse.openfga.test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.net.URI;
import java.util.List;

import jakarta.inject.Inject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.openfga.OpenFGAContainer;

import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.api.VertxWebClientFactory;
import io.quarkiverse.openfga.client.api.auth.UnauthenticatedCredentialsProvider;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.deployment.DevServicesOpenFGAConfig;
import io.quarkiverse.openfga.deployment.DevServicesOpenFGAProcessor;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.mutiny.core.Vertx;

@Testcontainers
public class OpenFGAClientSharedTest {

    @Container
    private static final OpenFGAContainer sharedContainer = new OpenFGAContainer(DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE)
            .withLabel(DevServicesOpenFGAProcessor.DEV_SERVICE_LABEL, DevServicesOpenFGAConfig.DEFAULT_SERVICE_NAME)
            .withExposedPorts(DevServicesOpenFGAProcessor.OPEN_FGA_EXPOSED_HTTP_PORT);

    private static List<String> createdStoreNames = List.of("dev", "testing1", "testing2");

    static void initSharedContainer() {
        System.setProperty("quarkus.openfga.test-launch-mode", LaunchMode.DEV_PROFILE);

        sharedContainer.start();

        var vertx = Vertx.vertx();
        try {
            var sharedURL = URI.create(sharedContainer.getHttpEndpoint()).toURL();
            var sharedClient = new OpenFGAClient(
                    new API(VertxWebClientFactory.create(sharedURL, vertx), UnauthenticatedCredentialsProvider.INSTANCE));

            var createdStores = Multi.createFrom().iterable(createdStoreNames)
                    .onItem().transformToUniAndConcatenate(sharedClient::createStore)
                    .collect().asList()
                    .subscribe().withSubscriber(UniAssertSubscriber.create())
                    .awaitItem()
                    .getItem();

            createdStoreNames = createdStores.stream().map(Store::getName).toList();

            assertThat(createdStoreNames)
                    .isNotNull()
                    .containsExactlyInAnyOrderElementsOf(createdStoreNames);
        } catch (Exception x) {
            throw new RuntimeException(x);
        } finally {
            vertx.closeAndAwait();
        }
    }

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setBeforeAllCustomizer(OpenFGAClientSharedTest::initSharedContainer)
            .overrideConfigKey("quarkus.profile", "dev")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    OpenFGAClient client;

    @Test
    @DisplayName("Can List Stores in Shared Container")
    public void canListStores() {

        var list = client.listStores()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .isNotNull()
                .extracting(PaginatedList::getItems, as(InstanceOfAssertFactories.collection(Store.class)))
                .map(Store::getName)
                .containsExactlyInAnyOrderElementsOf(createdStoreNames);
    }
}
