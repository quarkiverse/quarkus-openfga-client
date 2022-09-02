package io.quarkiverse.openfga.client.api;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.URL;

import org.jboss.logging.Logger;

import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.runtime.TlsConfig;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

public class VertxWebClientFactory {

    private static final Logger log = Logger.getLogger(VertxWebClientFactory.class.getName());

    public static WebClient create(OpenFGAConfig config, TlsConfig tlsConfig, boolean tracingEnabled, Vertx vertx) {

        var url = config.url;

        var options = new WebClientOptions()
                .setSsl("https".equals(url.getProtocol()))
                .setDefaultHost(url.getHost())
                .setDefaultPort(url.getPort() != -1 ? url.getPort() : url.getDefaultPort())
                .setConnectTimeout((int) config.connectTimeout.toMillis())
                .setIdleTimeout((int) config.readTimeout.getSeconds() * 2)
                .setTracingPolicy(tracingEnabled ? TracingPolicy.PROPAGATE : TracingPolicy.IGNORE);

        config.nonProxyHosts.ifPresent(options::setNonProxyHosts);

        boolean trustAll = config.tls.skipVerify.orElseGet(() -> tlsConfig.trustAll);
        if (trustAll) {
            skipVerify(options);
        } else {
            config.tls.caCert.ifPresent(caCert -> cacert(options, caCert));
        }

        return WebClient.create(vertx, options);
    }

    public static WebClient create(URL url, Vertx vertx) {

        var options = new WebClientOptions()
                .setSsl("https".equals(url.getProtocol()))
                .setDefaultHost(url.getHost())
                .setDefaultPort(url.getPort() != -1 ? url.getPort() : url.getDefaultPort())
                .setConnectTimeout((int) SECONDS.toMillis(2))
                .setIdleTimeout(2)
                .setIdleTimeoutUnit(SECONDS);

        return WebClient.create(vertx, options);
    }

    private static void cacert(WebClientOptions options, String cacert) {
        log.debug("configure tls with " + cacert);
        options.setTrustOptions(new PemTrustOptions().addCertPath(cacert));
    }

    private static void skipVerify(WebClientOptions options) {
        log.debug("configure tls with skip-verify");
        options.setTrustAll(true);
        options.setVerifyHost(false);
    }
}
