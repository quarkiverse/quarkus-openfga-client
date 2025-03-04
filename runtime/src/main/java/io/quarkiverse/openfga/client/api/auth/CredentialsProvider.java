package io.quarkiverse.openfga.client.api.auth;

import io.smallrye.mutiny.Uni;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.mutiny.ext.web.client.WebClient;

public interface CredentialsProvider {

    Uni<TokenCredentials> getTokenCredentials(WebClient webClient);

}
