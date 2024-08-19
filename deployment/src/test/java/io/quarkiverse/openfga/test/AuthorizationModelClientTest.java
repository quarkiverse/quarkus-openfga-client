package io.quarkiverse.openfga.test;

import static java.time.Duration.ofSeconds;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import java.util.Map;
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
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.model.*;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class AuthorizationModelClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    OpenFGAClient openFGAClient;

    Store store;

    StoreClient storeClient;

    AuthorizationModelClient authorizationModelClient;

    @BeforeEach
    public void createTestStoreAndModel() {
        store = openFGAClient.createStore("test").await().atMost(ofSeconds(10));
        storeClient = openFGAClient.store(store.getId());

        // ensure it has an auth model
        var useTypeDef = TypeDefinition.of("user", Map.of());

        var documentTypeDef = TypeDefinition.of("document", Map.of(
                "reader", Userset.direct("a", 1),
                "writer", Userset.direct("b", 2)),
                Metadata.of(
                        Map.of("reader", RelationMetadata.of(List.of(RelationReference.of("user"))),
                                "writer", RelationMetadata.of(List.of(RelationReference.of("user"))))));

        var schema = AuthorizationModelSchema.of(List.of(useTypeDef, documentTypeDef), null);

        var authModelId = storeClient.authorizationModels().create(schema)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        authorizationModelClient = storeClient.authorizationModels().model(authModelId);
    }

    @AfterEach
    public void deleteTestStore() {
        if (storeClient != null) {
            storeClient.delete().await().atMost(ofSeconds(10));
        }
    }

    @Test
    @DisplayName("Can Read & Write Tuples")
    public void canReadWriteTuples() {

        var tuples = List.of(
                ConditionalTupleKey.of("document:123", "reader", "user:me"));
        var writes = authorizationModelClient.write(tuples, emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var foundTuples = storeClient.readAllTuples()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem()
                .stream().map(Tuple::getKey).collect(Collectors.toList());

        assertThat(foundTuples)
                .isEqualTo(tuples);
    }

    @Test
    @DisplayName("Throws Validation Exception When Write User Not Object")
    public void throwsValidationExceptionWhenUserNotObject() {

        var tuples = List.of(
                ConditionalTupleKey.of("document:123", "reader", "user"));
        authorizationModelClient.write(tuples, emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(FGAValidationException.class,
                        "Invalid tuple 'document:123#reader@user'. Reason: the 'user' field must be an object (e.g. document:1) or an 'object#relation' or a typed wildcard (e.g. group:*)");
    }

    @Test
    @DisplayName("Can Execute Checks")
    public void canExecuteChecks() {

        var tuple = ConditionalTupleKey.of("document:123", "reader", "user:me");

        var writes = authorizationModelClient.write(List.of(tuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var allowed = authorizationModelClient.check(tuple.withoutCondition(), null)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(allowed)
                .isTrue();
    }

    @Test
    @DisplayName("Can Read All Relationships for an Object")
    public void canReadAllRelationshipsForObject() {

        var readerTuple = ConditionalTupleKey.of("document:123", "reader", "user:me");
        var writerTuple = ConditionalTupleKey.of("document:123", "writer", "user:me");

        authorizationModelClient.write(List.of(readerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(writerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.queryAllTuples(PartialTupleKey.of("document:123", null, null))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects)
                .hasSize(2)
                .anySatisfy(t -> {
                    assertThat(t.getKey()).isEqualTo(readerTuple);
                    assertThat(t.getTimestamp()).isCloseTo(now(), within(5, SECONDS));
                })
                .anySatisfy(t -> {
                    assertThat(t.getKey()).isEqualTo(writerTuple);
                    assertThat(t.getTimestamp()).isCloseTo(now(), within(5, SECONDS));
                });
    }

    @Test
    @DisplayName("Can Read All Relationships for an Object, and Relation")
    public void canReadAllRelationshipsForObjectAndRelation() {

        var meTuple = ConditionalTupleKey.of("document:123", "reader", "user:me");
        var youTuple = ConditionalTupleKey.of("document:123", "reader", "user:you");

        authorizationModelClient.write(List.of(meTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(youTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.queryAllTuples(PartialTupleKey.of("document:123", "reader", null))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects)
                .hasSize(2)
                .anySatisfy(t -> {
                    assertThat(t.getKey()).isEqualTo(meTuple);
                    assertThat(t.getTimestamp()).isCloseTo(now(), within(5, SECONDS));
                })
                .anySatisfy(t -> {
                    assertThat(t.getKey()).isEqualTo(youTuple);
                    assertThat(t.getTimestamp()).isCloseTo(now(), within(5, SECONDS));
                });
    }

    @Test
    @DisplayName("Can Read All Relationships for an Object, and User")
    public void canReadAllRelationshipsForObjectAndUser() {

        var readerTuple = ConditionalTupleKey.of("document:123", "reader", "user:me");
        var writerTuple = ConditionalTupleKey.of("document:123", "writer", "user:me");

        authorizationModelClient.write(List.of(readerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(writerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.queryAllTuples(PartialTupleKey.of("document:123", null, "user:me"))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects)
                .hasSize(2)
                .anySatisfy(t -> {
                    assertThat(t.getKey()).isEqualTo(readerTuple);
                    assertThat(t.getTimestamp()).isCloseTo(now(), within(5, SECONDS));
                })
                .anySatisfy(t -> {
                    assertThat(t.getKey()).isEqualTo(writerTuple);
                    assertThat(t.getTimestamp()).isCloseTo(now(), within(5, SECONDS));
                });
    }

    @Test
    @DisplayName("Can Read All Relationships for an Object Type, and User")
    public void canReadAllRelationshipsForObjectTypeAndUser() {

        var readerTuple = ConditionalTupleKey.of("document:123", "reader", "user:me");
        var writerTuple = ConditionalTupleKey.of("document:123", "writer", "user:me");

        authorizationModelClient.write(List.of(readerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(writerTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(ConditionalTupleKey.of("document:123", "writer", "user:you")), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.queryAllTuples(PartialTupleKey.of("document:", null, "user:me"))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects)
                .hasSize(2)
                .anySatisfy(t -> {
                    assertThat(t.getKey()).isEqualTo(readerTuple);
                    assertThat(t.getTimestamp()).isCloseTo(now(), within(5, SECONDS));
                })
                .anySatisfy(t -> {
                    assertThat(t.getKey()).isEqualTo(writerTuple);
                    assertThat(t.getTimestamp()).isCloseTo(now(), within(5, SECONDS));
                });
    }

    @Test
    @DisplayName("Can List All Objects for an Object Type, Relation, and User")
    public void canListAllRelationshipsForObjectTypeRelationAndUser() {

        var aTuple = ConditionalTupleKey.of("document:123", "writer", "user:me");
        var bTuple = ConditionalTupleKey.of("document:456", "writer", "user:me");

        authorizationModelClient.write(List.of(aTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();
        authorizationModelClient.write(List.of(bTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.listObjects("document", "writer", "user:me", null)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects)
                .containsOnly("document:123", "document:456");
    }

    @Test
    @DisplayName("Can List All Users for an Object Type, Relation, and User Type")
    public void canListAllUsersForObjectAndRelationship() {

        var aTuple = ConditionalTupleKey.of("document:123", "writer", "user:them");
        var bTuple = ConditionalTupleKey.of("document:456", "writer", "user:me");
        var cTuple = ConditionalTupleKey.of("document:456", "writer", "user:you");

        authorizationModelClient.write(List.of(aTuple, bTuple, cTuple), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient
                .listUsers(AnyObject.of("document", "456"), "writer", List.of(UserTypeFilter.of("user")))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects)
                .containsOnly(User.object("user", "me"), User.object("user", "you"));
    }
}
