package io.quarkiverse.openfga.client;

import static io.quarkiverse.openfga.client.utils.PaginatedList.collectAllPages;

import java.util.List;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.AuthorizationModel;
import io.quarkiverse.openfga.client.model.TypeDefinition;
import io.quarkiverse.openfga.client.model.TypeDefinitions;
import io.quarkiverse.openfga.client.model.dto.WriteAuthorizationModelResponse;
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
        return storeId.flatMap(storeId -> api.readAuthorizationModels(storeId, pageSize, pagingToken))
                .map(res -> new PaginatedList<>(res.getAuthorizationModels(), res.getContinuationToken()));
    }

    public Uni<List<AuthorizationModel>> listAll() {
        return listAll(null);
    }

    public Uni<List<AuthorizationModel>> listAll(@Nullable Integer pageSize) {
        return collectAllPages(pageSize, this::list);
    }

    @Deprecated
    public Uni<String> create(List<TypeDefinition> typeDefinitions) {
        return create(new TypeDefinitions(typeDefinitions));
    }

    public Uni<String> create(TypeDefinitions typeDefinitions) {
        return storeId.flatMap(storeId -> api.writeAuthorizationModel(storeId, typeDefinitions))
                .map(WriteAuthorizationModelResponse::getAuthorizationModelId);
    }

    public AuthorizationModelClient model(String authorizationModelId) {
        return new AuthorizationModelClient(api, storeId, authorizationModelId);
    }

    public AuthorizationModelClient defaultModel() {
        return new AuthorizationModelClient(api, storeId, null);
    }

}
