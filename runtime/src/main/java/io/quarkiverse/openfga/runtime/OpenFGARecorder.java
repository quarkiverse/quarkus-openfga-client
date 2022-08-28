package io.quarkiverse.openfga.runtime;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.OpenFGAClient;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkiverse.openfga.runtime.health.OpenFGAHealthCheck;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.TlsConfig;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.mutiny.core.Vertx;

@Recorder
public class OpenFGARecorder {

    public RuntimeValue<API> createAPI(OpenFGAConfig config, TlsConfig tlsConfig, boolean tracingEnabled,
            RuntimeValue<io.vertx.core.Vertx> vertx, ShutdownContext shutdownContext) {
        var api = new API(config, tlsConfig, tracingEnabled, Vertx.newInstance(vertx.getValue()));
        shutdownContext.addShutdownTask(api::close);
        return new RuntimeValue<>(api);
    }

    public RuntimeValue<OpenFGAClient> createClient(RuntimeValue<API> api) {
        OpenFGAClient openFGAClient = new OpenFGAClient(api.getValue());
        return new RuntimeValue<>(openFGAClient);
    }

    public RuntimeValue<StoreClient> createStoreClient(RuntimeValue<API> api, OpenFGAConfig config) {
        StoreClient storeClient = new StoreClient(api.getValue(), config.storeId);
        return new RuntimeValue<>(storeClient);
    }

    public RuntimeValue<AuthorizationModelClient> createAuthModelClient(RuntimeValue<API> api, OpenFGAConfig config) {
        AuthorizationModelClient authModelClient = new AuthorizationModelClient(api.getValue(), config.storeId,
                config.authorizationModelId.orElse(null));
        return new RuntimeValue<>(authModelClient);
    }

    public RuntimeValue<OpenFGAHealthCheck> createHealthCheck(RuntimeValue<API> api, OpenFGAConfig config) {
        OpenFGAHealthCheck healthCheck = new OpenFGAHealthCheck(api.getValue(), config.storeId);
        return new RuntimeValue<>(healthCheck);
    }
}
