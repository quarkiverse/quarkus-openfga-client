package io.quarkiverse.openfga.client.api;

import static io.quarkiverse.openfga.client.api.Queries.query;
import static io.quarkiverse.openfga.client.api.Vars.vars;
import static io.vertx.core.http.HttpMethod.*;
import static io.vertx.mutiny.core.http.HttpHeaders.ACCEPT;
import static io.vertx.mutiny.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

import java.io.Closeable;
import java.util.Map;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.dto.*;
import io.quarkiverse.openfga.client.model.utils.ModelMapper;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.tls.TlsConfigurationRegistry;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.codec.BodyCodec;
import io.vertx.mutiny.uritemplate.UriTemplate;
import io.vertx.mutiny.uritemplate.Variables;

public class API implements Closeable {

    private final WebClient webClient;
    @Nullable
    private final Credentials credentials;

    public API(OpenFGAConfig config, boolean tracingEnabled, Vertx vertx, TlsConfigurationRegistry tlsRegistry) {
        this(VertxWebClientFactory.create(config, tracingEnabled, vertx, tlsRegistry),
                config.sharedKey().map(TokenCredentials::new).orElse(null));
    }

    public API(WebClient webClient, @Nullable Credentials credentials) {
        this.webClient = webClient;
        this.credentials = credentials;
    }

    public void close() {
        webClient.close();
    }

    public Uni<ListStoresResponse> listStores(ListStoresRequest request) {
        return execute(
                request("List Stores",
                        GET,
                        STORES_URI,
                        vars(),
                        query(PAGE_SIZE_PARAM, request.getPageSize(), CONT_TOKEN_PARAM, request.getContinuationToken(),
                                NAME_PARAM, request.getName())),
                ExpectedStatus.OK,
                ListStoresResponse.class);
    }

    public Uni<CreateStoreResponse> createStore(CreateStoreRequest request) {
        return execute(
                request("Create Store",
                        POST,
                        STORES_URI,
                        vars()),
                request,
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

    public Uni<ListAuthorizationModelsResponse> listAuthorizationModels(String storeId,
            ListAuthorizationModelsRequest request) {
        return execute(
                request("Read Auth Models",
                        GET,
                        AUTH_MODELS_URI,
                        vars(STORE_ID_PARAM, storeId),
                        query(PAGE_SIZE_PARAM, request.getPageSize(), CONT_TOKEN_PARAM, request.getContinuationToken())),
                ExpectedStatus.OK,
                ListAuthorizationModelsResponse.class);
    }

    public Uni<WriteAuthorizationModelResponse> writeAuthorizationModel(String storeId,
            WriteAuthorizationModelRequest request) {
        return execute(
                request("Write Auth Model",
                        POST,
                        AUTH_MODELS_URI,
                        vars(STORE_ID_PARAM, storeId)),
                request,
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

    public Uni<ReadChangesResponse> readChanges(String storeId, ReadChangesRequest request) {
        return execute(
                request("Read Changes",
                        GET,
                        CHANGES_URI,
                        vars(STORE_ID_PARAM, storeId),
                        query(TYPE_PARAM, request.getType(), START_TIME_PARAM, request.getStartTime(),
                                PAGE_SIZE_PARAM, request.getPageSize(), CONT_TOKEN_PARAM,
                                request.getContinuationToken())),
                ExpectedStatus.OK,
                ReadChangesResponse.class);
    }

    public Uni<ReadResponse> read(String storeId, ReadRequest request) {
        return execute(
                request("Read",
                        POST,
                        READ_URI,
                        vars(STORE_ID_PARAM, storeId)),
                request,
                ExpectedStatus.OK,
                ReadResponse.class);
    }

    public Uni<WriteResponse> write(String storeId, WriteRequest request) {
        return execute(
                request("Write",
                        POST,
                        WRITE_URI,
                        vars(STORE_ID_PARAM, storeId)),
                request,
                ExpectedStatus.OK,
                WriteResponse.class);
    }

    public Uni<CheckResponse> check(String storeId, CheckRequest request) {
        return execute(
                request("Check",
                        POST,
                        CHECK_URI,
                        vars(STORE_ID_PARAM, storeId)),
                request,
                ExpectedStatus.OK,
                CheckResponse.class);
    }

    public Uni<BatchCheckResponse> batchCheck(String storeId, BatchCheckRequest request) {
        return execute(
                request("Batch Check",
                        POST,
                        BATCH_CHECK_URI,
                        vars(STORE_ID_PARAM, storeId)),
                request,
                ExpectedStatus.OK,
                BatchCheckResponse.class);
    }

    public Uni<ExpandResponse> expand(String storeId, ExpandRequest request) {
        return execute(
                request("Expand",
                        POST,
                        EXPAND_URI,
                        vars(STORE_ID_PARAM, storeId)),
                request,
                ExpectedStatus.OK,
                ExpandResponse.class);
    }

    public Uni<ListObjectsResponse> listObjects(String storeId, ListObjectsRequest request) {
        return execute(
                request("List Objects",
                        POST,
                        LIST_OBJECTS_URI,
                        vars(STORE_ID_PARAM, storeId)),
                request,
                ExpectedStatus.OK,
                ListObjectsResponse.class);
    }

    public Uni<ListUsersResponse> listUsers(String storeId, ListUsersRequest request) {
        return execute(
                request("List Users",
                        POST,
                        LIST_USERS_URI,
                        vars(STORE_ID_PARAM, storeId)),
                request,
                ExpectedStatus.OK,
                ListUsersResponse.class);
    }

    public Uni<ReadAssertionsResponse> readAssertions(String storeId, String authorizationModelId) {
        return execute(
                request("Read Assertions",
                        GET,
                        ASSERTIONS_URI,
                        vars(STORE_ID_PARAM, storeId, AUTH_MODEL_ID_PARAM, authorizationModelId)),
                ExpectedStatus.OK,
                ReadAssertionsResponse.class);
    }

    public Uni<Void> writeAssertions(String storeId, WriteAssertionsRequest request) {
        return execute(
                request("Write Assertions",
                        PUT,
                        ASSERTIONS_URI,
                        vars(STORE_ID_PARAM, storeId, AUTH_MODEL_ID_PARAM, request.getAuthorizationModelId())),
                request,
                ExpectedStatus.NO_CONTENT);
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
                .deferred(() -> {
                    try {
                        return prepare(request)
                                .putHeader(ACCEPT.toString(), APPLICATION_JSON)
                                .putHeader(CONTENT_TYPE.toString(), APPLICATION_JSON)
                                .sendBuffer(Buffer.buffer(ModelMapper.mapper.writeValueAsString(body)));
                    } catch (Throwable t) {
                        return Uni.createFrom().failure(t);
                    }
                })
                .onItem().transformToUni(response -> {
                    try {
                        checkJSONResponse(response, expectedStatus);
                        return Uni.createFrom().item(ModelMapper.mapper.readValue(response.bodyAsString(), responseType));
                    } catch (Throwable e) {
                        return Uni.createFrom().failure(e);
                    }
                });
    }

    private <B> Uni<Void> execute(HttpRequest<Buffer> request, B body, ExpectedStatus expectedStatus) {
        return Uni.createFrom()
                .deferred(() -> {
                    try {
                        return prepare(request)
                                .putHeader(CONTENT_TYPE.toString(), APPLICATION_JSON)
                                .sendBuffer(Buffer.buffer(ModelMapper.mapper.writeValueAsString(body)));
                    } catch (Throwable t) {
                        return Uni.createFrom().failure(t);
                    }
                })
                .onItem().transformToUni(response -> {
                    try {
                        checkStatus(response, expectedStatus);
                        return Uni.createFrom().nullItem();
                    } catch (Throwable e) {
                        return Uni.createFrom().failure(e);
                    }
                });
    }

    private <R> Uni<R> execute(HttpRequest<Buffer> request, ExpectedStatus expectedStatus, Class<R> responseType) {
        return prepare(request)
                .putHeader(ACCEPT.toString(), APPLICATION_JSON)
                .as(BodyCodec.buffer())
                .send()
                .onItem().transformToUni(response -> {
                    try {
                        checkJSONResponse(response, expectedStatus);
                        return Uni.createFrom().item(ModelMapper.mapper.readValue(response.bodyAsString(), responseType));
                    } catch (Throwable e) {
                        return Uni.createFrom().failure(e);
                    }
                });
    }

    private Uni<Void> execute(HttpRequest<Buffer> request, ExpectedStatus expectedStatus) {
        return prepare(request)
                .send()
                .onItem().transformToUni(response -> {
                    try {
                        checkStatus(response, expectedStatus);
                        return Uni.createFrom().nullItem();
                    } catch (Throwable e) {
                        return Uni.createFrom().failure(e);
                    }
                });
    }

    private <R> HttpRequest<R> prepare(HttpRequest<R> request) {

        if (credentials != null) {
            request.authentication(credentials);
        }

        return request;
    }

    private HttpRequest<Buffer> request(String operationName, HttpMethod method, UriTemplate uriTemplate, Variables variables,
            Map<String, String> query) {
        for (Map.Entry<String, String> entry : query.entrySet()) {
            variables.set(entry.getKey(), entry.getValue());
        }
        return request(operationName, method, uriTemplate, variables);
    }

    private HttpRequest<Buffer> request(String operationName, HttpMethod method, UriTemplate uriTemplate, Variables variables) {
        RequestOptions options = new RequestOptions()
                .setURI(uriTemplate.expandToString(variables))
                .setTraceOperation(format("FGA | %s", operationName.toUpperCase()));
        return webClient.request(method, options);
    }

    private static void checkJSONResponse(HttpResponse<Buffer> response, ExpectedStatus expectedStatus) throws Throwable {
        checkStatus(response, expectedStatus);
        checkJSON(response);
    }

    private static void checkStatus(HttpResponse<Buffer> response, ExpectedStatus expectedStatus) throws Throwable {
        if (response.statusCode() != expectedStatus.statusCode) {
            throw Errors.convert(response);
        }
    }

    private static void checkJSON(HttpResponse<Buffer> response) throws Throwable {
        String contentType = response.headers().get(HttpHeaders.CONTENT_TYPE);
        if (contentType == null) {
            throw new NoStackTraceThrowable("Missing response content type");
        }
        int paramIdx = contentType.indexOf(';');
        String mediaType = paramIdx != -1 ? contentType.substring(0, paramIdx) : contentType;
        if (mediaType.equalsIgnoreCase("application/json")) {
            return;
        }
        String message = "Expect content type " + contentType + " to be application/json";
        throw new NoStackTraceThrowable(message);
    }

    private static final String APPLICATION_JSON = "application/json";
    private static final String PAGE_SIZE_PARAM = "page_size";
    private static final String CONT_TOKEN_PARAM = "continuation_token";
    private static final String STORE_ID_PARAM = "store_id";
    private static final String AUTH_MODEL_ID_PARAM = "authorization_model_id";
    private static final String TYPE_PARAM = "type";
    private static final String NAME_PARAM = "name";
    private static final String START_TIME_PARAM = "start_time";
    private static final String ID_PARAM = "id";

    private static final UriTemplate STORES_URI = UriTemplate
            .of(format("/stores{?%s,%s,%s}", PAGE_SIZE_PARAM, CONT_TOKEN_PARAM, NAME_PARAM));
    private static final UriTemplate STORE_URI = UriTemplate.of("/stores/{store_id}");
    private static final UriTemplate ASSERTIONS_URI = UriTemplate.of("/stores/{store_id}/assertions/{authorization_model_id}");
    private static final UriTemplate AUTH_MODELS_URI = UriTemplate
            .of(format("/stores/{store_id}/authorization-models{?%s,%s}", PAGE_SIZE_PARAM, CONT_TOKEN_PARAM));
    private static final UriTemplate AUTH_MODEL_URI = UriTemplate.of("/stores/{store_id}/authorization-models/{id}");
    private static final UriTemplate CHANGES_URI = UriTemplate
            .of(format("/stores/{store_id}/changes{?%s,%s,%s,%s}", TYPE_PARAM, START_TIME_PARAM, PAGE_SIZE_PARAM,
                    CONT_TOKEN_PARAM));
    private static final UriTemplate CHECK_URI = UriTemplate.of("/stores/{store_id}/check");
    private static final UriTemplate BATCH_CHECK_URI = UriTemplate.of("/stores/{store_id}/batch-check");
    private static final UriTemplate EXPAND_URI = UriTemplate.of("/stores/{store_id}/expand");
    private static final UriTemplate LIST_OBJECTS_URI = UriTemplate.of("/stores/{store_id}/list-objects");
    private static final UriTemplate LIST_USERS_URI = UriTemplate.of("/stores/{store_id}/list-users");
    private static final UriTemplate READ_URI = UriTemplate.of("/stores/{store_id}/read");
    private static final UriTemplate WRITE_URI = UriTemplate.of("/stores/{store_id}/write");
    private static final UriTemplate HEALTH_URI = UriTemplate.of("/healthz");

}
