package io.quarkiverse.openfga.runtime.config;

import java.util.Optional;

public interface OpenFGATLSConfig {

    /**
     * Allows to bypass certificate validation on TLS communications.
     * <p>
     * If true this will allow TLS communications with OpenFGA, without checking the validity of the
     * certificate presented by OpenFGA. This is discouraged in production because it allows man in the middle
     * type of attacks.
     */
    Optional<Boolean> skipVerify();

    /**
     * Certificate bundle used to validate TLS communications with OpenFGA.
     * <p>
     * The path to a pem bundle file, if TLS is required, and trusted certificates are not set through
     * javax.net.ssl.trustStore system property.
     */
    Optional<String> caCert();

}
