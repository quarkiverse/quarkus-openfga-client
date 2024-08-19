package io.quarkiverse.openfga.client.model;

public enum ConsistencyPreference {
    // Default if not set. Behavior will be the same as MINIMIZE_LATENCY
    UNSPECIFIED,
    // Minimize latency at the potential expense of lower consistency.
    MINIMIZE_LATENCY,
    // Prefer higher consistency, at the potential expense of increased latency.
    HIGHER_CONSISTENCY
}
