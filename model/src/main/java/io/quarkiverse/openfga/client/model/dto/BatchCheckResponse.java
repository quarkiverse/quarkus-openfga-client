package io.quarkiverse.openfga.client.model.dto;

import java.util.Map;

import io.quarkiverse.openfga.client.model.CheckResult;

public record BatchCheckResponse(Map<String, CheckResult> result) {
}
