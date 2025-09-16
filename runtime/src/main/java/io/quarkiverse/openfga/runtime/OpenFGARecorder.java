package io.quarkiverse.openfga.runtime;

import static io.quarkiverse.openfga.client.OpenFGAClient.storeIdResolver;

import java.util.function.Supplier;

import jakarta.inject.Inject;

import io.quarkiverse.openfga.client.*;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.tls.TlsConfigurationRegistry;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;

@Recorder
public class OpenFGARecorder {

    private final RuntimeValue<OpenFGAConfig> configValue;

    @Inject
    public OpenFGARecorder(RuntimeValue<OpenFGAConfig> configValue) {
        this.configValue = configValue;
    }

    protected OpenFGARecorder() {
        configValue = null;
    }

    OpenFGAConfig getConfig() {
        return configValue.getValue();
    }

    public RuntimeValue<API> createAPI(boolean tracingEnabled,
            RuntimeValue<io.vertx.core.Vertx> vertx, Supplier<TlsConfigurationRegistry> tlsRegistry,
            ShutdownContext shutdownContext) {
        var api = new API(getConfig(), tracingEnabled, Vertx.newInstance(vertx.getValue()), tlsRegistry.get());
        shutdownContext.addShutdownTask(api::close);
        return new RuntimeValue<>(api);
    }

    public RuntimeValue<OpenFGAClient> createClient(RuntimeValue<API> api) {
        OpenFGAClient openFGAClient = new OpenFGAClient(api.getValue());
        return new RuntimeValue<>(openFGAClient);
    }

    public RuntimeValue<StoreClient> createStoreClient(RuntimeValue<API> api) {
        var config = getConfig();
        var storeIdResolver = storeIdResolver(api.getValue(), config.store(), config.alwaysResolveStoreId());
        StoreClient storeClient = new StoreClient(api.getValue(), storeIdResolver);
        return new RuntimeValue<>(storeClient);
    }

    public RuntimeValue<AuthorizationModelClient> createAuthModelClient(RuntimeValue<API> apiValue) {
        var config = getConfig();
        var api = apiValue.getValue();
        var configResolver = storeIdResolver(api, config.store(), config.alwaysResolveStoreId())
                .flatMap(storeId -> {
                    var authModelId = config.authorizationModelId();
                    if (authModelId.isPresent()) {
                        return Uni.createFrom().item(new ClientConfig(storeId, authModelId.get()));
                    } else {
                        return OpenFGAClient.authorizationModelIdResolver(api, storeId)
                                .map(modelId -> new ClientConfig(storeId, modelId));
                    }
                });
        var authModelClient = new AuthorizationModelClient(api, configResolver);
        return new RuntimeValue<>(authModelClient);
    }

    public RuntimeValue<AuthorizationModelsClient> createAuthModelsClient(RuntimeValue<API> api) {
        var config = getConfig();
        var storeIdResolver = storeIdResolver(api.getValue(), config.store(), config.alwaysResolveStoreId());
        var authModelsClient = new AuthorizationModelsClient(api.getValue(), storeIdResolver);
        return new RuntimeValue<>(authModelsClient);
    }

    public RuntimeValue<AssertionsClient> createAssertionsClient(RuntimeValue<API> api) {
        var config = getConfig();
        var configResolver = storeIdResolver(api.getValue(), config.store(), config.alwaysResolveStoreId())
                .flatMap(storeId -> OpenFGAClient.authorizationModelIdResolver(api.getValue(), storeId)
                        .map(modelId -> new ClientConfig(storeId, modelId)));
        var assertionsClient = new AssertionsClient(api.getValue(), configResolver);
        return new RuntimeValue<>(assertionsClient);
    }
}
