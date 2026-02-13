package io.quarkiverse.openfga.client;

import javax.annotation.Nullable;

public final class ClientConfig {
    private final String storeId;
    private final String authorizationModelId;

    public ClientConfig(String storeId, @Nullable String authorizationModelId) {
        this.storeId = storeId;
        this.authorizationModelId = authorizationModelId;
    }

    public String getStoreId() {
        return storeId;
    }

    public ClientConfig withStoreId(String storeId) {
        return new ClientConfig(storeId, authorizationModelId);
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public ClientConfig withAuthorizationModelId(String authorizationModelId) {
        return new ClientConfig(storeId, authorizationModelId);
    }
}
