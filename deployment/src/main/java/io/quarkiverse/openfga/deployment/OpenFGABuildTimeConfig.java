package io.quarkiverse.openfga.deployment;

import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = OpenFGAConfig.PREFIX)
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface OpenFGABuildTimeConfig {

    /**
     * Health check configuration.
     */
    interface Health {
        /**
         * Whether a health check is published in case the smallrye-health extension is present.
         */
        @WithDefault("true")
        boolean enabled();
    }

    /**
     * Health Check Configuration
     */
    @ConfigDocSection
    Health health();

    /**
     * Tracing configuration.
     */
    interface Tracing {
        /**
         * Whether tracing spans of client commands are reported.
         */
        @WithDefault("true")
        boolean enabled();
    }

    /**
     * Tracing Configuration
     */
    @ConfigDocSection
    Tracing tracing();

    /**
     * Dev Services Configuration
     */
    @ConfigDocSection
    DevServicesOpenFGAConfig devservices();

}
