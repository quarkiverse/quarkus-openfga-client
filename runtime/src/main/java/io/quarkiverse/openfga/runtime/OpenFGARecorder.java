package io.quarkiverse.openfga.runtime;

import static io.quarkiverse.openfga.client.OpenFGAClient.storeIdResolver;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.mutiny.core.Vertx;

@Recorder
public class OpenFGARecorder {

    public RuntimeValue<API> createAPI(OpenFGAConfig config, boolean tracingEnabled,
            RuntimeValue<io.vertx.core.Vertx> vertx, ShutdownContext shutdownContext) {
        var globalTrustAll = ConfigProvider.getConfig().getOptionalValue("quarkus.tls.trust-all", Boolean.class)
                .orElse(false);
        var api = new API(config, globalTrustAll, tracingEnabled, Vertx.newInstance(vertx.getValue()));
        shutdownContext.addShutdownTask(api::close);
        return new RuntimeValue<>(api);
    }

    public RuntimeValue<OpenFGAClient> createClient(RuntimeValue<API> api) {
        OpenFGAClient openFGAClient = new OpenFGAClient(api.getValue());
        return new RuntimeValue<>(openFGAClient);
    }

    public RuntimeValue<StoreClient> createStoreClient(RuntimeValue<API> api, OpenFGAConfig config) {
        var storeIdResolver = storeIdResolver(api.getValue(), config.store, config.alwaysResolveStoreId);
        StoreClient storeClient = new StoreClient(api.getValue(), storeIdResolver);
        return new RuntimeValue<>(storeClient);
    }

    public RuntimeValue<AuthorizationModelClient> createAuthModelClient(RuntimeValue<API> api, OpenFGAConfig config) {
        var storeIdResolver = storeIdResolver(api.getValue(), config.store, config.alwaysResolveStoreId);
        AuthorizationModelClient authModelClient = new AuthorizationModelClient(api.getValue(), storeIdResolver,
                config.authorizationModelId.orElse(null));
        return new RuntimeValue<>(authModelClient);
    }
}
