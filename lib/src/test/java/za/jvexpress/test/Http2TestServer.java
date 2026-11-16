package za.jvexpress.test;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.ServiceLoader;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

final class Http2TestServer implements AutoCloseable {

    static final Map<String, String> ROUTES = Map.of(
            "/hello", "hello",
            "/one", "one",
            "/two", "two",
            "/after-reset", "still-connected");

    private final Http2ServerAdapter.RunningServer running;
    private final SSLContext clientTlsContext;

    private Http2TestServer(
            Http2ServerAdapter.RunningServer running,
            SSLContext clientTlsContext) {
        this.running = running;
        this.clientTlsContext = clientTlsContext;
    }

    static Http2TestServer start() throws Exception {
        Http2TlsTestSupport.TlsContexts contexts = Http2TlsTestSupport.loadContexts();
        Http2ServerAdapter adapter = ServiceLoader.load(Http2ServerAdapter.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Register a test Http2ServerAdapter for the selected public HTTPS server API"));
        Http2ServerAdapter.RunningServer running = adapter.start(
                contexts.server(), ROUTES);
        return new Http2TestServer(running, contexts.client());
    }

    HttpClient client(HttpClient.Version version) {
        return client(version, null);
    }

    HttpClient client(HttpClient.Version version, SSLParameters sslParameters) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .sslContext(clientTlsContext)
                .connectTimeout(Duration.ofSeconds(3))
                .version(version);
        if (sslParameters != null) {
            builder.sslParameters(sslParameters);
        }
        return builder.build();
    }

    URI uri(String path) {
        return URI.create("https://localhost:%d%s".formatted(
                running.address().getPort(), path));
    }

    String authority() {
        return "localhost:" + running.address().getPort();
    }

    InetSocketAddress address() {
        return running.address();
    }

    SSLContext clientTlsContext() {
        return clientTlsContext;
    }

    @Override
    public void close() throws Exception {
        running.close();
    }
}
