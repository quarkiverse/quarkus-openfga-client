package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.dto.*;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.smallrye.mutiny.Uni;

public class AuthorizationModelsClient {

    private final API api;
    private final Uni<String> storeId;

    public AuthorizationModelsClient(API api, Uni<String> storeId) {
        this.api = api;
        this.storeId = storeId;
    }

    public Uni<PaginatedList<AuthorizationModel>> list(@Nullable Integer pageSize, @Nullable String pagingToken) {
        var request = ListAuthorizationModelsRequest.builder()
                .pageSize(pageSize)
                .continuationToken(pagingToken)
                .build();
        return storeId.flatMap(storeId -> api.listAuthorizationModels(storeId, request))
                .map(res -> new PaginatedList<>(res.getAuthorizationModels(), res.getContinuationToken()));
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
        return new AuthorizationModelClient(api, storeId, authorizationModelId);
    }

    public Uni<AuthorizationModelClient> defaultModel() {
        return storeId.flatMap(storeId -> {
            return api.listAuthorizationModels(storeId, ListAuthorizationModelsRequest.builder().pageSize(1).build())
                    .map(ListAuthorizationModelsResponse::getAuthorizationModels)
                    .flatMap(models -> {
                        if (models.isEmpty()) {
                            var notFound = new FGAValidationException(
                                    FGAValidationException.Code.LATEST_AUTHORIZATION_MODEL_NOT_FOUND,
                                    "No default authorization model found");
                            return Uni.createFrom().failure(notFound);
                        }
                        return Uni.createFrom().item(models.get(0).getId());
                    })
                    .map(id -> new AuthorizationModelClient(api, Uni.createFrom().item(storeId), id));
        });
    }

}
