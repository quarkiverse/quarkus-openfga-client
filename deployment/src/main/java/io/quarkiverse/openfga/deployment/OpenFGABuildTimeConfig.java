package io.quarkiverse.openfga.deployment;

import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = OpenFGAConfig.NAME, phase = ConfigPhase.BUILD_TIME)
public class OpenFGABuildTimeConfig {

    /**
     * Whether a health check is published in case the smallrye-health extension is present.
     */
    @ConfigItem(name = "health.enabled", defaultValue = "true")
    public boolean healthEnabled;

    /**
     * Whether tracing spans of client commands are reported.
     */
    @ConfigItem(name = "tracing.enabled")
    public boolean tracingEnabled;

    /**
     * Dev services configuration.
     */
    @ConfigItem
    public DevServicesOpenFGAConfig devservices;
}
