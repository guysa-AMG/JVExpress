package za.jvexpress.test;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

final class Http2TlsTestSupport {

    private static final char[] STORE_PASSWORD = "changeit".toCharArray();
    private static final String STORE_RESOURCE =
            "/za/jvexpress/test/http2-test-identity.p12";

    private Http2TlsTestSupport() {
    }

    static TlsContexts loadContexts() throws Exception {
        KeyStore identity = KeyStore.getInstance("PKCS12");
        try (InputStream input = Http2TlsTestSupport.class.getResourceAsStream(STORE_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Missing test TLS identity: " + STORE_RESOURCE);
            }
            identity.load(input, STORE_PASSWORD);
        }

        KeyManagerFactory keyManagers = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagers.init(identity, STORE_PASSWORD);

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry(
                "jvexpress-test",
                identity.getCertificate("jvexpress-test"));
        TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagers.init(trustStore);

        SSLContext server = SSLContext.getInstance("TLS");
        server.init(keyManagers.getKeyManagers(), trustManagers.getTrustManagers(), null);

        SSLContext client = SSLContext.getInstance("TLS");
        client.init(null, trustManagers.getTrustManagers(), null);
        return new TlsContexts(server, client);
    }

    record TlsContexts(SSLContext server, SSLContext client) {
    }
}
