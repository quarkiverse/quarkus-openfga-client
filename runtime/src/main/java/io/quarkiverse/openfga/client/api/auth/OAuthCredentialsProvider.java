package io.quarkiverse.openfga.client.api.auth;

import static io.quarkiverse.openfga.runtime.config.OpenFGAConfig.Credentials.OIDC.DEFAULT_TOKEN_ISSUER_PATH;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

import io.quarkiverse.openfga.client.model.dto.auth.CredentialsFlowRequest;
import io.quarkiverse.openfga.client.model.dto.auth.CredentialsFlowResponse;
import io.quarkiverse.openfga.client.model.utils.ModelMapper;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkiverse.openfga.runtime.config.OpenFGAConfig.Credentials.OIDC;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.ext.web.client.WebClient;

public class OAuthCredentialsProvider implements CredentialsProvider {

    private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";

    private final CredentialsFlowRequest tokenRequest;
    private final String tokenIssuerURL;
    private final Clock clock;
    private final Random random;
    private AccessToken currentAccessToken;

    public OAuthCredentialsProvider(OIDC config, Clock clock, Random random) {
        this.tokenRequest = new CredentialsFlowRequest(
                config.clientId(),
                config.clientSecret(),
                config.audience(),
                config.scopes().orElse(""),
                CLIENT_CREDENTIALS_GRANT_TYPE);
        this.tokenIssuerURL = config.tokenIssuer().resolve(config.tokenIssuerPath().orElse(DEFAULT_TOKEN_ISSUER_PATH))
                .toString();
        this.currentAccessToken = new AccessToken(
                "",
                Instant.EPOCH,
                config.tokenExpirationThreshold().orElse(OpenFGAConfig.DEFAULT_TOKEN_EXPIRATION_THRESHOLD),
                config.tokenExpirationThresholdJitter().orElse(OpenFGAConfig.DEFAULT_TOKEN_EXPIRATION_THRESHOLD_JITTER));
        this.clock = clock;
        this.random = random;
    }

    public Uni<AccessToken> getAccessToken(WebClient webClient) {
        if (currentAccessToken.isExpired(clock, random)) {
            return refreshAccessToken(webClient);
        }
        return Uni.createFrom().item(currentAccessToken);
    }

    public Uni<AccessToken> refreshAccessToken(WebClient webClient) {
        var options = new RequestOptions()
                .setAbsoluteURI(tokenIssuerURL)
                .setTraceOperation("FGA | OIDC - Token")
                .putHeader("Content-Type", "application/x-www-form-urlencoded")
                .putHeader("Accept", "application/json");
        return webClient.request(HttpMethod.POST, options)
                .sendForm(MultiMap.caseInsensitiveMultiMap().addAll(tokenRequest.toForm()))
                .onItem().transformToUni(response -> {
                    try {
                        if (response.statusCode() != 200) {
                            throw new HttpException(response.statusCode(), response.statusMessage());
                        }
                        return Uni.createFrom()
                                .item(ModelMapper.mapper.readValue(response.bodyAsString(), CredentialsFlowResponse.class));
                    } catch (Throwable e) {
                        return Uni.createFrom().failure(e);
                    }
                })
                .onItem().transform(response -> {
                    currentAccessToken = currentAccessToken.refresh(
                            response.accessToken(),
                            Optional.of(response.expiresIn())
                                    .map(expiresIn -> Instant.now(clock).plusSeconds(expiresIn)).orElse(null));
                    return currentAccessToken;
                });
    }

    @Override
    public Uni<TokenCredentials> getTokenCredentials(WebClient webClient) {
        var accessToken = getAccessToken(webClient);
        return accessToken.onItem().transform(token -> new TokenCredentials(token.token()));
    }

}
