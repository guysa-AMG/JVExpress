package za.jvexpress.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Socket;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Http2TlsAlpnTest {

    private Http2TestServer server;

    @BeforeEach
    void startServer() throws Exception {
        server = Http2TestServer.start();
    }

    @AfterEach
    void stopServer() throws Exception {
        if (server != null) {
            server.close();
        }
    }

    @Test
    void negotiatesHttp2OverTrustedTls() throws Exception {
        HttpResponse<String> response = server.client(HttpClient.Version.HTTP_2)
                .send(HttpRequest.newBuilder(server.uri("/hello")).GET().build(),
                        HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpClient.Version.HTTP_2, response.version());
        assertEquals(200, response.statusCode());
        assertEquals("hello", response.body());
        assertTls12OrNewer(response.sslSession().orElseThrow());
    }

    @Test
    void preservesHttp11WhenClientRequestsIt() throws Exception {
        HttpResponse<String> response = server.client(HttpClient.Version.HTTP_1_1)
                .send(HttpRequest.newBuilder(server.uri("/hello")).GET().build(),
                        HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpClient.Version.HTTP_1_1, response.version());
        assertEquals(200, response.statusCode());
        assertEquals("hello", response.body());
        assertTls12OrNewer(response.sslSession().orElseThrow());
    }

    @Test
    void supportsTls12AndHttp11Alpn() throws Exception {
        SSLParameters parameters = new SSLParameters();
        parameters.setProtocols(new String[] { "TLSv1.2" });
        parameters.setApplicationProtocols(new String[] { "http/1.1" });

        HttpResponse<String> response = server.client(
                        HttpClient.Version.HTTP_1_1, parameters)
                .send(HttpRequest.newBuilder(server.uri("/hello")).GET().build(),
                        HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpClient.Version.HTTP_1_1, response.version());
        assertEquals(200, response.statusCode());
        assertEquals("hello", response.body());
        assertEquals("TLSv1.2", response.sslSession().orElseThrow().getProtocol());
    }

    @Test
    void rejectsTls11WhenTheClientRuntimeCanOfferIt() throws Exception {
        try (SSLSocket socket = (SSLSocket) server.clientTlsContext()
                .getSocketFactory()
                .createSocket("localhost", server.address().getPort())) {
            SSLParameters parameters = socket.getSSLParameters();
            parameters.setProtocols(new String[] { "TLSv1.1" });
            parameters.setApplicationProtocols(new String[] { "http/1.1" });
            socket.setSSLParameters(parameters);

            try {
                socket.startHandshake();
            } catch (SSLException failure) {
                if (isLocallyDisabledProtocol(failure)) {
                    Assumptions.abort(
                            "The JDK client security policy disables TLS 1.1 before it can be offered");
                }
                return;
            }
            throw new AssertionError("Server accepted TLS 1.1");
        }
    }

    @Test
    void doesNotAcceptCleartextOnTheTlsListener() throws Exception {
        byte[] preface = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n"
                .getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        try (Socket socket = new Socket("localhost", server.address().getPort())) {
            socket.setSoTimeout(2_000);
            socket.getOutputStream().write(preface);
            socket.getOutputStream().flush();
            assertEquals(-1, socket.getInputStream().read(),
                    "TLS listener must not return an HTTP response to cleartext HTTP/2");
        } catch (java.net.SocketException rejected) {
            // A reset is also a valid rejection of cleartext on the TLS port.
        }
    }

    private static boolean isLocallyDisabledProtocol(SSLException failure) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            String message = cause.getMessage();
            if (message != null
                    && (message.contains("No appropriate protocol")
                    || message.contains("protocol is disabled"))) {
                return true;
            }
        }
        return false;
    }

    private static void assertTls12OrNewer(SSLSession session) {
        String protocol = session.getProtocol().toUpperCase(Locale.ROOT);
        assertTrue(protocol.equals("TLSV1.2") || protocol.equals("TLSV1.3"),
                () -> "Expected TLS 1.2 or newer but negotiated " + session.getProtocol());
    }
}
