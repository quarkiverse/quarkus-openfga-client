package io.quarkiverse.openfga.client;

public final class ClientConfig {
    private final String storeId;
    private final String authorizationModelId;

    public ClientConfig(String storeId, String authorizationModelId) {
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
