package io.quarkiverse.openfga.client.api.auth;

import io.smallrye.mutiny.Uni;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.mutiny.ext.web.client.WebClient;

public class PresharedKeyCredentialsProvider implements CredentialsProvider {

    public final String sharedKey;

    public PresharedKeyCredentialsProvider(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    @Override
    public Uni<TokenCredentials> getTokenCredentials(WebClient webClient) {
        return Uni.createFrom().item(new TokenCredentials(sharedKey));
    }
}
