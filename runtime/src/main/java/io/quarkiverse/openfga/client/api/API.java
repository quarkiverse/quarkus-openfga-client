package io.quarkiverse.openfga.client.api;

import static io.quarkiverse.openfga.client.api.Queries.query;
import static io.quarkiverse.openfga.client.api.Vars.vars;
import static io.smallrye.mutiny.unchecked.Unchecked.function;
import static io.smallrye.mutiny.unchecked.Unchecked.supplier;
import static io.vertx.core.http.HttpMethod.*;
import static io.vertx.mutiny.core.http.HttpHeaders.ACCEPT;
import static io.vertx.mutiny.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.quarkiverse.openfga.client.model.TypeDefinitions;
import io.quarkiverse.openfga.client.model.dto.*;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.runtime.TlsConfig;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.client.predicate.ResponsePredicate;
import io.vertx.mutiny.ext.web.codec.BodyCodec;
import io.vertx.mutiny.uritemplate.UriTemplate;
import io.vertx.mutiny.uritemplate.Variables;

public class API implements Closeable {

    private final WebClient webClient;
    private final Optional<Credentials> credentials;
    private final ObjectMapper objectMapper;

    public API(OpenFGAConfig config, TlsConfig tlsConfig, boolean tracingEnabled, Vertx vertx) {
        this(VertxWebClientFactory.create(config, tlsConfig, tracingEnabled, vertx),
                config.sharedKey.map(TokenCredentials::new));
    }

    public API(WebClient webClient, Optional<Credentials> credentials) {
        this.webClient = webClient;
        this.credentials = credentials;
        this.objectMapper = createObjectMapper();
    }

    public void close() {
        webClient.close();
    }

    public TypeDefinitions parseModel(String modelJSON) throws IOException {
        return objectMapper.readValue(modelJSON, TypeDefinitions.class);
    }

    public Uni<ListStoresResponse> listStores(@Nullable Integer pageSize, @Nullable String continuationToken) {
        return execute(
                request("List Stores",
                        GET,
                        STORES_URI,
                        vars(),
                        query(PAGE_SIZE_PARAM, pageSize, CONT_TOKEN_PARAM, continuationToken)),
                ExpectedStatus.OK,
                ListStoresResponse.class);
    }

    public Uni<CreateStoreResponse> createStore(CreateStoreRequest body) {
        return execute(
                request("Create Store",
                        POST,
                        STORES_URI,
                        vars()),
                body,
                ExpectedStatus.CREATED,
                CreateStoreResponse.class);
    }

    public Uni<GetStoreResponse> getStore(String storeId) {
        return execute(
                request("Get Store",
                        GET,
                        STORE_URI,
                        vars(STORE_ID_PARAM, storeId)),
                ExpectedStatus.OK,
                GetStoreResponse.class);
    }

    public Uni<Void> deleteStore(String storeId) {
        return execute(
                request("Delete Store",
                        DELETE,
                        STORE_URI,
                        vars(STORE_ID_PARAM, storeId)),
                ExpectedStatus.NO_CONTENT);
    }

    public Uni<ReadAssertionsResponse> readAssertions(String storeId, @Nullable String authorizationModelId) {
        return execute(
                request("Read Assertions",
                        GET,
                        ASSERTIONS_URI,
                        vars(STORE_ID_PARAM, storeId, AUTH_MODEL_ID_PARAM, authorizationModelId)),
                ExpectedStatus.OK,
                ReadAssertionsResponse.class);
    }

    public Uni<Void> writeAssertions(String storeId, String authorizationModelId, WriteAssertionsRequest body) {
        return execute(
                request("Write Assertions",
                        PUT,
                        ASSERTIONS_URI,
                        vars(STORE_ID_PARAM, storeId, AUTH_MODEL_ID_PARAM, authorizationModelId)),
                body,
                ExpectedStatus.NO_CONTENT);
    }

    public Uni<ReadAuthorizationModelsResponse> readAuthorizationModels(String storeId, @Nullable Integer pageSize,
            @Nullable String continuationToken) {
        return execute(
                request("Read Auth Models",
                        GET,
                        AUTH_MODELS_URI,
                        vars(STORE_ID_PARAM, storeId),
                        query(PAGE_SIZE_PARAM, pageSize, CONT_TOKEN_PARAM, continuationToken)),
                ExpectedStatus.OK,
                ReadAuthorizationModelsResponse.class);
    }

    public Uni<WriteAuthorizationModelResponse> writeAuthorizationModel(String storeId, TypeDefinitions typeDefinitions) {
        return execute(
                request("Write Auth Model",
                        POST,
                        AUTH_MODELS_URI,
                        vars(STORE_ID_PARAM, storeId)),
                typeDefinitions,
                ExpectedStatus.CREATED,
                WriteAuthorizationModelResponse.class);
    }

    public Uni<ReadAuthorizationModelResponse> readAuthorizationModel(String storeId, @Nullable String id) {
        return execute(
                request("Read Auth Model",
                        GET,
                        AUTH_MODEL_URI,
                        vars(STORE_ID_PARAM, storeId, ID_PARAM, id)),
                ExpectedStatus.OK,
                ReadAuthorizationModelResponse.class);
    }

    public Uni<ReadChangesResponse> readChanges(String storeId, @Nullable String type, @Nullable Integer pageSize,
            @Nullable String continuationToken) {
        return execute(
                request("Read Changes",
                        GET,
                        CHANGES_URI,
                        vars(STORE_ID_PARAM, storeId),
                        query(TYPE_PARAM, type, PAGE_SIZE_PARAM, pageSize, CONT_TOKEN_PARAM, continuationToken)),
                ExpectedStatus.OK,
                ReadChangesResponse.class);
    }

    public Uni<CheckResponse> check(String storeId, CheckBody body) {
        return execute(
                request("Check",
                        POST,
                        CHECK_URI,
                        vars(STORE_ID_PARAM, storeId)),
                body,
                ExpectedStatus.OK,
                CheckResponse.class);
    }

    public Uni<ExpandResponse> expand(String storeId, ExpandBody body) {
        return execute(
                request("Expand",
                        POST,
                        EXPAND_URI,
                        vars(STORE_ID_PARAM, storeId)),
                body,
                ExpectedStatus.OK,
                ExpandResponse.class);
    }

    public Uni<ListObjectsResponse> listObjects(String storeId, ListObjectsBody body) {
        return execute(
                request("List Objects",
                        POST,
                        LIST_OBJECTS_URI,
                        vars(STORE_ID_PARAM, storeId)),
                body,
                ExpectedStatus.OK,
                ListObjectsResponse.class);
    }

    public Uni<ReadResponse> read(String storeId, ReadBody body) {
        return execute(
                request("Read",
                        POST,
                        READ_URI,
                        vars(STORE_ID_PARAM, storeId)),
                body,
                ExpectedStatus.OK,
                ReadResponse.class);
    }

    public Uni<ReadTuplesResponse> readTuples(String storeId, ReadTuplesBody body) {
        return execute(
                request("Read Tuples",
                        POST,
                        READ_TUPLES_URI,
                        vars(STORE_ID_PARAM, storeId)),
                body,
                ExpectedStatus.OK,
                ReadTuplesResponse.class);
    }

    public Uni<WriteResponse> write(String storeId, WriteBody body) {
        return execute(
                request("Write",
                        POST,
                        WRITE_URI,
                        vars(STORE_ID_PARAM, storeId)),
                body,
                ExpectedStatus.OK,
                WriteResponse.class);
    }

    public Uni<HealthzResponse> health() {
        return execute(
                request(
                        "Health Check",
                        GET,
                        HEALTH_URI,
                        vars()),
                ExpectedStatus.OK,
                HealthzResponse.class);
    }

    private <B, R> Uni<R> execute(HttpRequest<Buffer> request, B body, ExpectedStatus expectedStatus, Class<R> responseType) {
        return Uni.createFrom()
                .deferred(supplier(() -> {
                    return prepare(request, expectedStatus)
                            .putHeader(ACCEPT.toString(), APPLICATION_JSON)
                            .expect(ResponsePredicate.JSON)
                            .putHeader(CONTENT_TYPE.toString(), APPLICATION_JSON)
                            .sendBuffer(Buffer.buffer(objectMapper.writeValueAsString(body)));
                }))
                .onItem().transform(function(response -> objectMapper.readValue(response.bodyAsString(), responseType)));
    }

    private <B> Uni<Void> execute(HttpRequest<Buffer> request, B body, ExpectedStatus expectedStatus) {
        return Uni.createFrom()
                .deferred(supplier(() -> {
                    return prepare(request, expectedStatus)
                            .putHeader(CONTENT_TYPE.toString(), APPLICATION_JSON)
                            .sendBuffer(Buffer.buffer(objectMapper.writeValueAsString(body)))
                            .replaceWithVoid();
                }));
    }

    private <R> Uni<R> execute(HttpRequest<Buffer> request, ExpectedStatus expectedStatus, Class<R> responseType) {
        return prepare(request, expectedStatus)
                .putHeader(ACCEPT.toString(), APPLICATION_JSON)
                .expect(ResponsePredicate.JSON)
                .as(BodyCodec.buffer())
                .send()
                .onItem().transform(function(response -> objectMapper.readValue(response.bodyAsString(), responseType)));
    }

    private Uni<Void> execute(HttpRequest<Buffer> request, ExpectedStatus expectedStatus) {
        return prepare(request, expectedStatus)
                .send()
                .replaceWithVoid();
    }

    private <R> HttpRequest<R> prepare(HttpRequest<R> request, ExpectedStatus expectedStatus) {

        // Add creds
        credentials.ifPresent(request::authentication);

        return request.expect(expectedStatus.responsePredicate);
    }

    private HttpRequest<Buffer> request(String operationName, HttpMethod method, UriTemplate uriTemplate, Variables variables,
            Map<String, String> query) {
        variables.set(FULL_QUERY_PARAM, query);
        return request(operationName, method, uriTemplate, variables);
    }

    private HttpRequest<Buffer> request(String operationName, HttpMethod method, UriTemplate uriTemplate, Variables variables) {
        RequestOptions options = new RequestOptions()
                .setURI(uriTemplate.expandToString(variables))
                .setTraceOperation(format("FGA | %s", operationName.toUpperCase()));
        return webClient.request(method, options);
    }

    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .registerModule(new ParameterNamesModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private static final String APPLICATION_JSON = "application/json";
    private static final String PAGE_SIZE_PARAM = "page_size";
    private static final String CONT_TOKEN_PARAM = "continuation_token";
    private static final String STORE_ID_PARAM = "store_id";
    private static final String AUTH_MODEL_ID_PARAM = "authorization_model_id";
    private static final String TYPE_PARAM = "type";
    private static final String ID_PARAM = "id";
    private static final String FULL_QUERY_PARAM = "query";

    private static final UriTemplate STORES_URI = UriTemplate.of("/stores{?query*}");
    private static final UriTemplate STORE_URI = UriTemplate.of("/stores/{store_id}");
    private static final UriTemplate ASSERTIONS_URI = UriTemplate.of("/stores/{store_id}/assertions/{authorization_model_id}");
    private static final UriTemplate AUTH_MODELS_URI = UriTemplate.of("/stores/{store_id}/authorization-models{?query*}");
    private static final UriTemplate AUTH_MODEL_URI = UriTemplate.of("/stores/{store_id}/authorization-models/{id}");
    private static final UriTemplate CHANGES_URI = UriTemplate.of("/stores/{store_id}/changes");
    private static final UriTemplate CHECK_URI = UriTemplate.of("/stores/{store_id}/check");
    private static final UriTemplate EXPAND_URI = UriTemplate.of("/stores/{store_id}/expand");
    private static final UriTemplate LIST_OBJECTS_URI = UriTemplate.of("/stores/{store_id}/list-objects");
    private static final UriTemplate READ_URI = UriTemplate.of("/stores/{store_id}/read");
    private static final UriTemplate WRITE_URI = UriTemplate.of("/stores/{store_id}/write");
    private static final UriTemplate READ_TUPLES_URI = UriTemplate.of("/stores/{store_id}/read-tuples");
    private static final UriTemplate HEALTH_URI = UriTemplate.of("/healthz");

}
