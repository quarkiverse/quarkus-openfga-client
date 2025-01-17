package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.AuthorizationModel;
import io.quarkiverse.openfga.client.model.AuthorizationModelSchema;
import io.quarkiverse.openfga.client.model.Condition;
import io.quarkiverse.openfga.client.model.TypeDefinition;
import io.quarkiverse.openfga.client.model.dto.ListAuthorizationModelsRequest;
import io.quarkiverse.openfga.client.model.dto.WriteAuthorizationModelRequest;
import io.quarkiverse.openfga.client.model.dto.WriteAuthorizationModelResponse;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.quarkiverse.openfga.client.utils.Pagination;
import io.smallrye.mutiny.Uni;

public class AuthorizationModelsClient {

    private final API api;
    private final Uni<String> storeId;

    public AuthorizationModelsClient(API api, Uni<String> storeId) {
        this.api = api;
        this.storeId = storeId;
    }

    @Deprecated(since = "2.4.0", forRemoval = true)
    public Uni<PaginatedList<AuthorizationModel>> list(@Nullable Integer pageSize, @Nullable String pagingToken) {
        return list(Pagination.limitedTo(pageSize).andContinuingFrom(pagingToken));
    }

    public Uni<PaginatedList<AuthorizationModel>> list() {
        return list(Pagination.DEFAULT);
    }

    public Uni<PaginatedList<AuthorizationModel>> list(Pagination pagination) {
        return storeId.flatMap(storeId -> {
            var request = ListAuthorizationModelsRequest.builder()
                    .pageSize(pagination.pageSize())
                    .continuationToken(pagination.continuationToken())
                    .build();
            return api.listAuthorizationModels(storeId, request);
        }).map(res -> new PaginatedList<>(res.getAuthorizationModels(), res.getContinuationToken()));
    }

    public Uni<List<AuthorizationModel>> listAll() {
        return listAll(null);
    }

    public Uni<List<AuthorizationModel>> listAll(@Nullable Integer pageSize) {
        return collectAllPages(pageSize, this::list);
    }

    public Uni<String> create(AuthorizationModelSchema schema) {
        return create(schema.getSchemaVersion(), schema.getTypeDefinitions(), schema.getConditions());
    }

    public Uni<String> create(String schemaVersion, List<TypeDefinition> typeDefinitions,
            @Nullable Map<String, Condition> conditions) {
        var request = WriteAuthorizationModelRequest.builder()
                .schemaVersion(schemaVersion)
                .typeDefinitions(typeDefinitions)
                .conditions(conditions)
                .build();
        return storeId.flatMap(storeId -> api.writeAuthorizationModel(storeId, request))
                .map(WriteAuthorizationModelResponse::getAuthorizationModelId);
    }

    public AuthorizationModelClient model(String authorizationModelId) {
        return new AuthorizationModelClient(api, storeId.map(storeId -> new ClientConfig(storeId, authorizationModelId)));
    }

    public AuthorizationModelClient defaultModel() {
        var config = storeId.flatMap(storeId -> OpenFGAClient.authorizationModelIdResolver(api, storeId)
                .map(modelId -> new ClientConfig(storeId, modelId)));
        return new AuthorizationModelClient(api, config);
    }

}
