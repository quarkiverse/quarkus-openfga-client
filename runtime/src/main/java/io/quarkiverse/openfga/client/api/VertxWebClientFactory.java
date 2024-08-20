package io.quarkiverse.openfga.client.api;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.URL;

import org.jboss.logging.Logger;

import io.quarkiverse.openfga.runtime.config.OpenFGAConfig;
import io.quarkus.tls.TlsConfiguration;
import io.quarkus.tls.TlsConfigurationRegistry;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

public class VertxWebClientFactory {

    private static final Logger log = Logger.getLogger(VertxWebClientFactory.class.getName());

    public static WebClient create(OpenFGAConfig config, boolean tracingEnabled, Vertx vertx,
            TlsConfigurationRegistry tlsRegistry) {

        var url = config.url();

        var options = new WebClientOptions()
                .setSsl("https".equals(url.getProtocol()))
                .setDefaultHost(url.getHost())
                .setDefaultPort(url.getPort() != -1 ? url.getPort() : url.getDefaultPort())
                .setConnectTimeout((int) config.connectTimeout().toMillis())
                .setIdleTimeout((int) config.readTimeout().getSeconds() * 2)
                .setTracingPolicy(tracingEnabled ? TracingPolicy.PROPAGATE : TracingPolicy.IGNORE);

        config.nonProxyHosts().ifPresent(options::setNonProxyHosts);

        TlsConfiguration.from(tlsRegistry, config.tlsConfigurationName())
                .or(tlsRegistry::getDefault)
                .ifPresent(tlsConfig -> {

                    options.setTrustOptions(tlsConfig.getTrustStoreOptions());
                    options.setTrustAll(tlsConfig.isTrustAll());
                    tlsConfig.getHostnameVerificationAlgorithm().ifPresent(algo -> {
                        options.setVerifyHost(algo.equals("NONE"));
                    });

                    // mutual TLS
                    options.setKeyCertOptions(tlsConfig.getKeyStoreOptions());

                    var sslOptions = tlsConfig.getSSLOptions();
                    if (sslOptions != null) {
                        options.setSslHandshakeTimeout(sslOptions.getSslHandshakeTimeout());
                        options.setSslHandshakeTimeoutUnit(sslOptions.getSslHandshakeTimeoutUnit());
                        for (var suite : sslOptions.getEnabledCipherSuites()) {
                            options.addEnabledCipherSuite(suite);
                        }
                        for (var buffer : sslOptions.getCrlValues()) {
                            options.addCrlValue(buffer);
                        }
                        options.setEnabledSecureTransportProtocols(sslOptions.getEnabledSecureTransportProtocols());
                        options.setUseAlpn(sslOptions.isUseAlpn());
                    }
                });

        return WebClient.create(vertx, options);
    }

    public static WebClient create(URL url, Vertx vertx) {

        var options = new WebClientOptions()
                .setSsl("https".equals(url.getProtocol()))
                .setDefaultHost(url.getHost())
                .setDefaultPort(url.getPort() != -1 ? url.getPort() : url.getDefaultPort())
                .setConnectTimeout((int) SECONDS.toMillis(2))
                .setIdleTimeout(2)
                .setIdleTimeoutUnit(SECONDS)
                .setTrustAll(true);

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
