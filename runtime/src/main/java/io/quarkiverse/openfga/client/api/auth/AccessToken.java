package io.quarkiverse.openfga.client.api.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import javax.annotation.Nullable;

public record AccessToken(
        String token,
        @Nullable Instant expiresAt,
        Duration expirationThreshold,
        Duration expirationThresholdJitter) {

    public boolean isExpired(Clock clock, Random random) {
        // If the token does not have an expiration date, it is intended to be valid forever
        if (expiresAt == null) {
            return false;
        }
        var jitter = Duration.ofMillis(random.nextLong(expirationThresholdJitter.toMillis()));
        return expiresAt.isBefore(clock.instant().plus(expirationThreshold).plus(jitter));
    }

    public AccessToken refresh(String token, @Nullable Instant expiresAt) {
        return new AccessToken(token, expiresAt, expirationThreshold, expirationThresholdJitter);
    }

}
