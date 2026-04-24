package io.quarkiverse.openfga.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.logging.LogRecord;

import jakarta.inject.Inject;

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
import io.quarkiverse.openfga.client.model.AuthorizationModelSchema;
import io.quarkiverse.openfga.client.model.RelTupleKeyed;
import io.quarkiverse.openfga.client.model.RelTupleKeys;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.client.model.dto.CreateStoreRequest;
import io.quarkiverse.openfga.client.model.dto.WriteAuthorizationModelRequest;
import io.quarkiverse.openfga.client.model.dto.WriteRequest;
import io.quarkiverse.openfga.deployment.DevServicesOpenFGAConfig;
import io.quarkiverse.openfga.deployment.DevServicesOpenFGAProcessor;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.mutiny.core.Vertx;

@Testcontainers
public class OpenFGAClientSharedDriftWarningTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Container
    static final OpenFGAContainer sharedContainer = new OpenFGAContainer(DevServicesOpenFGAProcessor.OPEN_FGA_IMAGE)
            .withLabel(DevServicesOpenFGAProcessor.DEV_SERVICE_LABEL, DevServicesOpenFGAConfig.DEFAULT_SERVICE_NAME)
            .withExposedPorts(DevServicesOpenFGAProcessor.OPEN_FGA_EXPOSED_HTTP_PORT);

    static void initSharedContainer() {
        System.setProperty("io.quarkiverse.openfga.test-launch-mode", LaunchMode.DEV_PROFILE);
        sharedContainer.start();

        var vertx = Vertx.vertx();
        try {
            var sharedURL = URI.create(sharedContainer.getHttpEndpoint()).toURL();
            try (var api = new API(VertxWebClientFactory.create(sharedURL, vertx),
                    UnauthenticatedCredentialsProvider.INSTANCE)) {
                var storeId = api.createStore(CreateStoreRequest.builder().name("dev").build())
                        .await().atMost(TIMEOUT).id();

                var schema = AuthorizationModelSchema.parse(readResource("auth-model.json"));
                var authModelId = api.writeAuthorizationModel(storeId, WriteAuthorizationModelRequest.builder()
                        .schemaVersion(schema.getSchemaVersion())
                        .typeDefinitions(schema.getTypeDefinitions())
                        .conditions(schema.getConditions())
                        .build()).await().atMost(TIMEOUT).authorizationModelId();

                var tupleKeys = loadTuples("auth-tuples-shared.json");
                api.write(storeId, WriteRequest.builder()
                        .authorizationModelId(authModelId)
                        .writes(WriteRequest.Writes.of(tupleKeys))
                        .build())
                        .await().atMost(TIMEOUT);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize shared OpenFGA container", e);
        } finally {
            vertx.closeAndAwait();
        }
    }

    private static Collection<RelTupleKeyed> loadTuples(String resourceName) {
        try {
            return RelTupleKeys.parseList(readResource(resourceName)).getTupleKeys();
        } catch (IOException e) {
            throw new RuntimeException("Could not parse tuples resource: " + resourceName, e);
        }
    }

    private static String readResource(String resourceName) {
        try (var stream = OpenFGAClientSharedDriftWarningTest.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (stream == null) {
                throw new IllegalStateException("Missing resource: " + resourceName);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Could not read resource: " + resourceName, e);
        }
    }

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setBeforeAllCustomizer(OpenFGAClientSharedDriftWarningTest::initSharedContainer)
            .overrideConfigKey("quarkus.profile", "dev")
            .overrideConfigKey("quarkus.openfga.devservices.authorization-model-location", "classpath:auth-model.json")
            .overrideConfigKey("quarkus.openfga.devservices.authorization-tuples-location", "classpath:auth-tuples-drift.json")
            .setLogRecordPredicate(OpenFGAClientSharedDriftWarningTest::isDriftWarningRecord)
            .assertLogRecords(OpenFGAClientSharedDriftWarningTest::assertDriftWarningRecord)
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("auth-model.json")
                    .addAsResource("auth-tuples-shared.json")
                    .addAsResource("auth-tuples-drift.json"));

    @Inject
    OpenFGAClient client;

    private static boolean isDriftWarningRecord(LogRecord record) {
        return record.getMessage() != null
                && record.getMessage().contains("initialization arguments differ");
    }

    private static void assertDriftWarningRecord(Collection<LogRecord> records) {
        assertThat(records)
                .anySatisfy(record -> {
                    assertThat(record.getMessage())
                            .contains("initialization arguments differ");
                    assertThat(record.getParameters())
                            .isNotNull()
                            .anySatisfy(parameter -> assertThat(String.valueOf(parameter)).contains("authorization tuples"));
                });
    }

    @Test
    @DisplayName("Warns when shared initialization tuples differ")
    public void warnsWhenSharedInitializationTuplesDiffer() {
        var stores = client.listAllStores()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(stores)
                .extracting(Store::getName)
                .contains("dev");
    }
}
