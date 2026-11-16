package za.jvexpress.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Http2MultiplexingTest {

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
    void servesConcurrentRequestsWithoutMixingStreamResponses() throws Exception {
        HttpClient client = server.client(HttpClient.Version.HTTP_2);
        CompletableFuture<HttpResponse<String>> first = client.sendAsync(
                HttpRequest.newBuilder(server.uri("/one")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        CompletableFuture<HttpResponse<String>> second = client.sendAsync(
                HttpRequest.newBuilder(server.uri("/two")).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        CompletableFuture.allOf(first, second).get(5, TimeUnit.SECONDS);
        HttpResponse<String> firstResponse = first.join();
        HttpResponse<String> secondResponse = second.join();

        assertEquals(HttpClient.Version.HTTP_2, firstResponse.version());
        assertEquals(HttpClient.Version.HTTP_2, secondResponse.version());
        assertEquals(200, firstResponse.statusCode());
        assertEquals(200, secondResponse.statusCode());
        assertEquals("one", firstResponse.body());
        assertEquals("two", secondResponse.body());
        assertTrue(firstResponse.sslSession().orElseThrow().isValid());
        assertTrue(secondResponse.sslSession().orElseThrow().isValid());
    }

    @Test
    void multiplexesStreamsOnOneTlsConnection() throws Exception {
        try (Http2WireClient client = Http2WireClient.connect(server)) {
            client.start();
            client.sendGet(1, "/one");
            client.sendGet(3, "/two");

            Map<Integer, String> responses =
                    client.readResponseBodies(Set.of(1, 3));
            assertEquals("one", responses.get(1));
            assertEquals("two", responses.get(3));
        }
    }
}
