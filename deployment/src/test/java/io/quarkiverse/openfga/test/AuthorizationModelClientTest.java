package io.quarkiverse.openfga.test;

import static io.quarkiverse.openfga.test.SchemaFixtures.*;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.*;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.AuthorizationModelClient.*;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.CheckResult;
import io.quarkiverse.openfga.client.model.schema.*;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.client.utils.Pagination;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public class AuthorizationModelClientTest {

    // Start unit test with extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(SchemaFixtures.class, Unchecked.class));

    @Inject
    OpenFGAClient openFGAClient;

    Store store;

    StoreClient storeClient;
    AuthorizationModelClient authorizationModelClient;

    private AuthorizationModelClient createAuthorizationModelClient(AuthorizationModelSchema schema) {

        var authModelId = storeClient.authorizationModels().create(schema)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        return storeClient.authorizationModels().model(authModelId);
    }

    @BeforeEach
    public void createTestStoreAndModel() {
        store = openFGAClient.createStore("test").await().atMost(ofSeconds(10));
        storeClient = openFGAClient.store(store.getId());
        authorizationModelClient = createAuthorizationModelClient(SchemaFixtures.schema);
    }

    @AfterEach
    public void deleteTestStore() {
        if (storeClient != null) {
            storeClient.delete().await().atMost(ofSeconds(10));
        }
    }

    @Test
    @DisplayName("Read & Write Tuples")
    public void readWriteTuples() {

        var tupleDef = document123.define(RelationshipNames.READER, userMe);

        var writes = authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var list = storeClient.readTuples()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(1)
                .extracting(RelTuple::getKey)
                .containsExactly(tupleDef.conditional());
        assertThat(list)
                .extracting(PaginatedList::getToken, as(STRING))
                .isEmpty();
    }

    @Test
    @DisplayName("Throws Validation Exception When Write User Invalid")
    public void throwsValidationExceptionWhenUserNotObject() {

        var tupleDef = document123.define(RelationshipNames.READER, Unchecked.user("user", ""));

        authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(FGAInputValidationException.class,
                        "Invalid tuple 'document:123#reader@user:'. " +
                                "Reason: the 'user' field is malformed (validation_error)");
    }

    @Test
    @DisplayName("Check Successful")
    public void checkSuccessful() {

        var tupleDef = document123.define(RelationshipNames.READER, userMe);

        var writes = authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var allowed = authorizationModelClient.check(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(allowed)
                .isTrue();
    }

    @Test
    @DisplayName("Check Failing")
    public void checkFailing() {

        var tupleDef = document123.define(RelationshipNames.READER, userMe);

        var disallowedTupleDef = document123.define(RelationshipNames.READER, userYou);

        var writes = authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var allowed = authorizationModelClient.check(disallowedTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(allowed)
                .isFalse();
    }

    @Test
    @DisplayName("Check With Contextual Tuples")
    public void checkWithContextualTuples() {

        var tupleDef = document123.define(RelationshipNames.READER, userMe);
        var ctxTupleDef = document123.define(RelationshipNames.OWNER, userYou);

        var writes = authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var allowed = authorizationModelClient.check(ctxTupleDef, CheckOptions.withContextualTuples(ctxTupleDef))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(allowed)
                .isTrue();
    }

    @Test
    @DisplayName("Check With Conditions")
    public void checkWithConditions() {

        var authorizationModelClient = createAuthorizationModelClient(SchemaFixtures.schemaWithCondition);

        var condTupleDef = document123.define(RelationshipNames.GRANTEE, userMe)
                .withCondition(RelCondition.of(ConditionNames.NON_EXPIRED_GRANT, Map.of(
                        ParameterNames.GRANT_TIME, "2021-01-01T00:00:00Z",
                        ParameterNames.GRANT_DURATION, "1h")));

        var writes = authorizationModelClient.write(condTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var allowed = authorizationModelClient.check(condTupleDef, CheckOptions.withContext(Map.of(
                ParameterNames.CURRENT_TIME, "2021-01-01T00:20:00Z")))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(allowed)
                .isTrue();
    }

    @Test
    @DisplayName("Batch Check")
    public void batchCheck() {

        var tupleDef1 = document123.define(RelationshipNames.READER, userMe);
        var tupleDef2 = document123.define(RelationshipNames.WRITER, userYou);
        var invTupleDef = document456.define(RelationshipNames.WRITER, Unchecked.user("user", ""));

        var writes = authorizationModelClient.write(tupleDef1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var results = authorizationModelClient.batchCheck(List.of(
                Check.builder()
                        .correlationId("1")
                        .tupleKey(tupleDef1)
                        .build(),
                Check.builder()
                        .correlationId("2")
                        .tupleKey(tupleDef2)
                        .build(),
                Check.builder()
                        .correlationId("3")
                        .tupleKey(invTupleDef)
                        .build()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(results)
                .hasEntrySatisfying("1", r -> {
                    assertThat(r).extracting(CheckResult::allowed, as(BOOLEAN)).isTrue();
                    assertThat(r).extracting(CheckResult::error).isNull();
                })
                .hasEntrySatisfying("2", r -> {
                    assertThat(r).extracting(CheckResult::allowed, as(BOOLEAN)).isFalse();
                    assertThat(r).extracting(CheckResult::error).isNull();
                })
                .hasEntrySatisfying("3", r -> {
                    assertThat(r).extracting(CheckResult::allowed, as(BOOLEAN)).isFalse();
                    assertThat(r)
                            .extracting(CheckResult::error)
                            .isNotNull()
                            .satisfies(e -> {
                                assertThat(e)
                                        .extracting(CheckError::inputError)
                                        .isEqualTo(InputErrorCode.VALIDATION_ERROR);
                                assertThat(e)
                                        .extracting(CheckError::message)
                                        .isEqualTo("the 'user' field is malformed");
                            });
                });
    }

    @Test
    @DisplayName("Expand")
    public void expand() {

        var tupleDef = document123.define(RelationshipNames.READER, userMe);

        var writes = authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var expanded = authorizationModelClient.expand(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(expanded)
                .isNotNull()
                .satisfies(t -> {
                    assertThat(t)
                            .extracting(UsersetTree::getRoot, as(type(Node.class)))
                            .satisfies(u -> {
                                assertThat(u)
                                        .extracting(Node::name)
                                        .isEqualTo("document:123#reader");
                                assertThat(u)
                                        .extracting(Node::leaf, as(type(Leaf.class)))
                                        .isNotNull()
                                        .extracting(Leaf::users, as(type(Users.class)))
                                        .isNotNull()
                                        .extracting(Users::users, as(iterable(String.class)))
                                        .isNotNull()
                                        .containsExactlyInAnyOrder(userMe.toString());
                                assertThat(u)
                                        .extracting(Node::computed)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::tupleToUserset)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::difference)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::union)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::intersection)
                                        .isNull();
                            });
                });
    }

    @Test
    @DisplayName("Expand With Contextual Tuples")
    public void expandWithContextualTuples() {

        var tupleDef = document123.define(RelationshipNames.READER, userMe);
        var ctxTupleDef = document123.define(RelationshipNames.READER, userYou);

        var writes = authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var expanded = authorizationModelClient.expand(tupleDef, ExpandOptions.withContextualTuples(ctxTupleDef))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(expanded)
                .isNotNull()
                .satisfies(t -> {
                    assertThat(t)
                            .extracting(UsersetTree::getRoot, as(type(Node.class)))
                            .satisfies(u -> {
                                assertThat(u)
                                        .extracting(Node::name)
                                        .isEqualTo("document:123#reader");
                                assertThat(u)
                                        .extracting(Node::leaf, as(type(Leaf.class)))
                                        .isNotNull()
                                        .extracting(Leaf::users, as(type(Users.class)))
                                        .isNotNull()
                                        .extracting(Users::users, as(iterable(String.class)))
                                        .isNotNull()
                                        .containsExactlyInAnyOrder(userMe.toString(), userYou.toString());
                                assertThat(u)
                                        .extracting(Node::computed)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::tupleToUserset)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::difference)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::union)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::intersection)
                                        .isNull();
                            });
                });
    }

    @Test
    @DisplayName("Expand With Conditions")
    public void expandWithConditions() {

        var authorizationModelClient = createAuthorizationModelClient(SchemaFixtures.schemaWithCondition);

        var tupleDef = document123.define(RelationshipNames.GRANTEE, userMe)
                .withCondition(RelCondition.of(ConditionNames.NON_EXPIRED_GRANT, Map.of(
                        ParameterNames.GRANT_TIME, "2021-01-01T00:00:00Z",
                        ParameterNames.GRANT_DURATION, "1h")));

        var writes = authorizationModelClient.write(tupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(writes.entrySet())
                .isEmpty();

        var expanded = authorizationModelClient.expand(tupleDef, ExpandOptions.withContext(Map.of(
                ParameterNames.CURRENT_TIME, "2021-01-01T00:20:00Z")))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(expanded)
                .isNotNull()
                .satisfies(t -> {
                    assertThat(t)
                            .extracting(UsersetTree::getRoot, as(type(Node.class)))
                            .satisfies(u -> {
                                assertThat(u)
                                        .extracting(Node::name)
                                        .isEqualTo("document:123#grantee");
                                assertThat(u)
                                        .extracting(Node::leaf, as(type(Leaf.class)))
                                        .isNotNull()
                                        .extracting(Leaf::users, as(type(Users.class)))
                                        .isNotNull()
                                        .extracting(Users::users, as(iterable(String.class)))
                                        .isNotNull()
                                        .containsExactlyInAnyOrder(userMe.toString());
                                assertThat(u)
                                        .extracting(Node::computed)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::tupleToUserset)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::difference)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::union)
                                        .isNull();
                                assertThat(u)
                                        .extracting(Node::intersection)
                                        .isNull();
                            });
                });
    }

    @Test
    @DisplayName("Read Tuples")
    public void readTuples() {

        var readerTupleDef = document123.define(RelationshipNames.READER, userMe);
        var writerTupleDef = document123.define(RelationshipNames.WRITER, userMe);
        var otherTupleDef = document456.define(RelationshipNames.WRITER, userMe);

        authorizationModelClient.write(readerTupleDef, writerTupleDef, otherTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(3)
                .extracting(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTupleDef.conditional(), writerTupleDef.conditional(),
                        otherTupleDef.conditional());
        assertThat(list)
                .extracting(PaginatedList::getToken, as(STRING))
                .isEmpty();
    }

    @Test
    @DisplayName("Read Tuples Matching Object")
    public void readTuplesMatchingObject() {

        var readerTupleDef = document123.define(RelationshipNames.READER, userMe);
        var writerTupleDef = document123.define(RelationshipNames.WRITER, userMe);
        var otherTupleDef = document456.define(RelationshipNames.WRITER, userMe);

        authorizationModelClient.write(readerTupleDef, writerTupleDef, otherTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObject(document123))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .extracting(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTupleDef.conditional(), writerTupleDef.conditional());
        assertThat(list)
                .extracting(PaginatedList::getToken, as(STRING))
                .isEmpty();
    }

    @Test
    @DisplayName("Read Tuples Matching Object and Relation")
    public void readTuplesMatchingObjectAndRelation() {

        var meTupleDef = document123.define(RelationshipNames.READER, userMe);
        var youTupleDef = document123.define(RelationshipNames.READER, userYou);
        var otherTupleDef = document456.define(RelationshipNames.WRITER, userMe);

        authorizationModelClient.write(meTupleDef, youTupleDef, otherTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObject(document123).relation(RelationshipNames.READER))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .extracting(RelTuple::getKey)
                .containsExactlyInAnyOrder(meTupleDef.conditional(), youTupleDef.conditional());
        assertThat(list)
                .extracting(PaginatedList::getToken, as(STRING))
                .isEmpty();
    }

    @Test
    @DisplayName("Read Tuples Matching Object and User")
    public void readTuplesMatchingObjectUser() {

        var readerTupleDef = document123.define(RelationshipNames.READER, userMe);
        var writerTupleDef = document123.define(RelationshipNames.WRITER, userMe);
        var otherTupleDef = document123.define(RelationshipNames.WRITER, userYou);

        authorizationModelClient.write(readerTupleDef, writerTupleDef, otherTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObject(document123).user(userMe))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(InstanceOfAssertFactories.collection(RelTuple.class)))
                .hasSize(2)
                .extracting(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTupleDef.conditional(), writerTupleDef.conditional());
        assertThat(list)
                .extracting(PaginatedList::getToken, as(STRING))
                .isEmpty();
    }

    @Test
    @DisplayName("Read Tuples Matching Object Type and User")
    public void readTuplesMatchingForObjectTypeAndUser() {

        var readerTupleDef = document123.define("reader", userMe);
        var writerTupleDef = document123.define("writer", userMe);
        var otherTupleDef = document123.define("writer", userYou);

        authorizationModelClient.write(List.of(readerTupleDef, writerTupleDef, otherTupleDef), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.read(ReadFilter.byObjectType(documentType).user(userMe))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .extracting(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTupleDef.conditional(), writerTupleDef.conditional());
        assertThat(list)
                .extracting(PaginatedList::getToken, as(STRING))
                .isEmpty();
    }

    @Test
    @DisplayName("Read Tuples With Pagination")
    public void readTuplesWithPagination() {

        var readerTupleDef = document123.define("reader", userMe);
        var writerTupleDef = document123.define("writer", userMe);
        var otherTupleDef = document123.define("owner", userMe);

        authorizationModelClient.write(List.of(readerTupleDef, writerTupleDef, otherTupleDef), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var page1 = authorizationModelClient.read(ReadFilter.byObjectType(documentType).user(userMe),
                Pagination.limitedTo(2))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(page1)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(2)
                .extracting(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTupleDef.conditional(), writerTupleDef.conditional());
        assertThat(page1)
                .extracting(PaginatedList::getToken, as(STRING))
                .isNotBlank();

        var page2 = authorizationModelClient.read(ReadFilter.byObjectType(documentType).user(userMe),
                Pagination.limitedTo(2).andContinuingFrom(page1.getToken()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(page2)
                .extracting(PaginatedList::getItems, as(iterable(RelTuple.class)))
                .hasSize(1)
                .extracting(RelTuple::getKey)
                .containsExactlyInAnyOrder(otherTupleDef.conditional());
        assertThat(page2)
                .extracting(PaginatedList::getToken, as(STRING))
                .isBlank();
    }

    @Test
    @DisplayName("Read All Tuples")
    public void readAllTuples() {

        var readerTupleDef = document123.define("reader", userMe);
        var writerTupleDef = document123.define("writer", userMe);
        var otherTupleDef = document123.define("owner", userMe);

        authorizationModelClient.write(List.of(readerTupleDef, writerTupleDef, otherTupleDef), emptyList())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient.readAll(ReadFilter.ALL, 1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .hasSize(3)
                .extracting(RelTuple::getKey)
                .containsExactlyInAnyOrder(readerTupleDef.conditional(), writerTupleDef.conditional(),
                        otherTupleDef.conditional());

        var list2 = authorizationModelClient.readAll()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(list2)
                .hasSize(3);

        var list3 = authorizationModelClient.readAll(ReadFilter.ALL)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(list3)
                .hasSize(3);
    }

    @Test
    @DisplayName("List Objects Matching Object Type, Relation, and User")
    public void listObjectsMatchingObjectTypeRelationAndUser() {

        var aTupleDef = document123.define(RelationshipNames.WRITER, userMe);
        var bTupleDef = document456.define(RelationshipNames.WRITER, userMe);
        var cTupleDef = document456.define(RelationshipNames.WRITER, userYou);

        authorizationModelClient.write(aTupleDef, bTupleDef, cTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.listObjects(ListObjectsFilter.byObjectType(documentType)
                .relation(RelationshipNames.WRITER)
                .user(userMe))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects)
                .containsOnly(document123, document456);
    }

    @Test
    @DisplayName("List Objects With Contextual Tuples")
    public void listObjectsWithContextualTuples() {

        var aTupleDef = document123.define(RelationshipNames.WRITER, userMe);
        var bTupleDef = document123.define(RelationshipNames.WRITER, userYou);
        var cTupleDef = document456.define(RelationshipNames.WRITER, userMe);

        authorizationModelClient.write(aTupleDef, bTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var objects = authorizationModelClient.listObjects(ListObjectsFilter.byObjectType(documentType)
                .relation(RelationshipNames.WRITER)
                .user(userMe),
                ListOptions.withContextualTuples(cTupleDef))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();
        assertThat(objects)
                .containsOnly(document123, document456);
    }

    @Test
    @DisplayName("List All Users for an Object, Relation, and User Type")
    public void listAllUsersForObjectRelationUserType() {

        var aTupleDef = document123.define("writer", userMe);
        var bTupleDef = document456.define("writer", userMe);
        var cTupleDef = document456.define("writer", userYou);

        authorizationModelClient.write(aTupleDef, bTupleDef, cTupleDef)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem();

        var list = authorizationModelClient
                .listUsers(ListUsersFilter.byObject(document456).relation("writer").userType(userType))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .getItem();

        assertThat(list)
                .containsOnly(User.object("user", "me"), User.object("user", "you"));
    }
}
