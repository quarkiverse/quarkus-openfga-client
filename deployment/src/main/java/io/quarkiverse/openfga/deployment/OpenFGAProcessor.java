package io.quarkiverse.openfga.deployment;

import static io.quarkus.deployment.Capability.SMALLRYE_HEALTH;
import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkiverse.openfga.client.*;
import io.quarkiverse.openfga.client.api.API;
import io.quarkiverse.openfga.client.model.Store;
import io.quarkiverse.openfga.runtime.OpenFGARecorder;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkiverse.openfga.runtime.health.OpenFGAHealthCheck;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.*;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;
import io.quarkus.tls.deployment.spi.TlsRegistryBuildItem;
import io.quarkus.vertx.deployment.VertxBuildItem;

class OpenFGAProcessor {

    static final String FEATURE = "openfga-client";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerModelClasses(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
            CombinedIndexBuildItem combinedIndexBuildItem) {

        final String[] modelClasses = combinedIndexBuildItem.getIndex()
                .getKnownClasses().stream()
                .filter(c -> c.name().packagePrefix() != null &&
                        c.name().packagePrefix().startsWith(Store.class.getPackageName()))
                .map(c -> c.name().toString())
                .toArray(String[]::new);
        reflectiveClasses.produce(ReflectiveClassBuildItem.weakClass(modelClasses));
    }

    @BuildStep
    @Record(RUNTIME_INIT)
    ServiceStartBuildItem registerSyntheticBeans(
            OpenFGABuildTimeConfig buildTimeConfig,
            OpenFGAConfig runtimeConfig,
            VertxBuildItem vertxBuildItem,
            Optional<TlsRegistryBuildItem> tlsRegistryBuildItem,
            ShutdownContextBuildItem shutdownContextBuildItem,
            OpenFGARecorder recorder,
            Capabilities capabilities,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeans,
            BuildProducer<ExtensionSslNativeSupportBuildItem> sslNativeSupport) {

        var tlsRegistrySupplier = tlsRegistryBuildItem.map(TlsRegistryBuildItem::registry)
                .orElse(() -> null);

        var apiValue = recorder.createAPI(
                runtimeConfig, buildTimeConfig.tracing().enabled(),
                vertxBuildItem.getVertx(), tlsRegistrySupplier,
                shutdownContextBuildItem);

        sslNativeSupport.produce(new ExtensionSslNativeSupportBuildItem(FEATURE));

        syntheticBeans.produce(
                SyntheticBeanBuildItem.configure(API.class)
                        .scope(ApplicationScoped.class)
                        .setRuntimeInit()
                        .runtimeValue(apiValue)
                        .done());

        syntheticBeans.produce(
                SyntheticBeanBuildItem.configure(OpenFGAClient.class)
                        .scope(ApplicationScoped.class)
                        .setRuntimeInit()
                        .runtimeValue(recorder.createClient(apiValue))
                        .done());

        syntheticBeans.produce(
                SyntheticBeanBuildItem.configure(StoreClient.class)
                        .scope(ApplicationScoped.class)
                        .setRuntimeInit()
                        .runtimeValue(recorder.createStoreClient(apiValue, runtimeConfig))
                        .done());

        syntheticBeans.produce(
                SyntheticBeanBuildItem.configure(AuthorizationModelClient.class)
                        .scope(ApplicationScoped.class)
                        .setRuntimeInit()
                        .runtimeValue(recorder.createAuthModelClient(apiValue, runtimeConfig))
                        .done());

        syntheticBeans.produce(
                SyntheticBeanBuildItem.configure(AuthorizationModelsClient.class)
                        .scope(ApplicationScoped.class)
                        .setRuntimeInit()
                        .runtimeValue(recorder.createAuthModelsClient(apiValue, runtimeConfig))
                        .done());

        syntheticBeans.produce(
                SyntheticBeanBuildItem.configure(AssertionsClient.class)
                        .scope(ApplicationScoped.class)
                        .setRuntimeInit()
                        .runtimeValue(recorder.createAssertionsClient(apiValue, runtimeConfig))
                        .done());

        return new ServiceStartBuildItem("openfga-client");
    }

    @BuildStep
    void registerHealthCheck(
            OpenFGABuildTimeConfig buildTimeConfig,
            Capabilities capabilities,
            BuildProducer<HealthBuildItem> health) {

        if (capabilities.isPresent(SMALLRYE_HEALTH)) {

            health.produce(new HealthBuildItem(OpenFGAHealthCheck.class.getName(), buildTimeConfig.health().enabled()));
        }

    }
}
