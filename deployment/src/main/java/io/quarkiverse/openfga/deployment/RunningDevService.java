package io.quarkiverse.openfga.deployment;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import io.quarkus.deployment.builditem.DevServicesResultBuildItem;

/**
 * A dev service instance managed by this extension (started or discovered), reported to Quarkus as a discovered service.
 */
final class RunningDevService implements Closeable {

    private final String name;
    private final String description;
    private final String containerId;
    private final Closeable closeable;
    private final Map<String, String> config;

    RunningDevService(String name, String description, String containerId, Closeable closeable,
            Map<String, String> config) {
        this.name = name;
        this.description = description;
        this.containerId = containerId;
        this.closeable = closeable;
        this.config = config;
    }

    DevServicesResultBuildItem toBuildItem() {
        return DevServicesResultBuildItem.discovered()
                .name(name)
                .description(description)
                .containerId(containerId)
                .config(config)
                .build();
    }

    @Override
    public void close() throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }
}
