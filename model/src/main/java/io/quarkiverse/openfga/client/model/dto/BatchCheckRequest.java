package io.quarkiverse.openfga.client.model.dto;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.Check;
import io.quarkiverse.openfga.client.model.ConsistencyPreference;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class BatchCheckRequest {

    public static final class Builder {

        @Nullable
        private Collection<Check> checks;
        @Nullable
        private String authorizationModelId;
        @Nullable
        private ConsistencyPreference consistency;

        private Builder() {
        }

        public Builder checks(Collection<Check> checks) {
            this.checks = checks;
            return this;
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder consistency(@Nullable ConsistencyPreference consistency) {
            this.consistency = consistency;
            return this;
        }

        public BatchCheckRequest build() {
            return new BatchCheckRequest(
                    Preconditions.parameterNonNull(checks, "checks"),
                    authorizationModelId, consistency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Collection<Check> checks;
    @Nullable
    private final String authorizationModelId;
    @Nullable
    private final ConsistencyPreference consistency;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    BatchCheckRequest(Collection<Check> checks,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @Nullable ConsistencyPreference consistency) {
        this.checks = checks;
        this.authorizationModelId = authorizationModelId;
        this.consistency = consistency;
    }

    public Collection<Check> getChecks() {
        return checks;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @Nullable
    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof BatchCheckRequest that))
            return false;
        return Objects.equals(checks, that.checks) &&
                Objects.equals(authorizationModelId, that.authorizationModelId) &&
                consistency == that.consistency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(checks, authorizationModelId, consistency);
    }

    @Override
    public String toString() {
        return "BatchCheckRequest[" +
                "checks=" + checks + ", " +
                "authorizationModelId=" + authorizationModelId + ", " +
                "consistency=" + consistency + ']';
    }

}
