package za.jvexpress.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Http2ProtocolSecurityTest {

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
    void rejectsMalformedClientPreface() throws Exception {
        try (Http2WireClient client = Http2WireClient.connect(server)) {
            client.writeBytes("NOT AN HTTP/2 PREFACE".getBytes(StandardCharsets.US_ASCII));
            assertEquals(Http2WireClient.PROTOCOL_ERROR, client.readProtocolError());
        }
    }

    @Test
    void rejectsDataOnAnIdleStream() throws Exception {
        try (Http2WireClient client = Http2WireClient.connect(server)) {
            client.start();
            client.sendDataOnIdleStream(1);
            assertEquals(Http2WireClient.PROTOCOL_ERROR, client.readProtocolError());
        }
    }

    @Test
    void rejectsConnectionSpecificHeadersOnHttp2Requests() throws Exception {
        try (Http2WireClient client = Http2WireClient.connect(server)) {
            client.start();
            client.sendRequestWithConnectionHeader(1, "/hello");
            assertEquals(Http2WireClient.PROTOCOL_ERROR, client.readProtocolError());
        }
    }

    @Test
    void rejectsZeroIncrementWindowUpdates() throws Exception {
        try (Http2WireClient client = Http2WireClient.connect(server)) {
            client.start();
            client.sendZeroWindowUpdate();
            assertEquals(Http2WireClient.PROTOCOL_ERROR, client.readProtocolError());
        }
    }

    @Test
    void rejectsFrameLargerThanAdvertisedMaximum() throws Exception {
        try (Http2WireClient client = Http2WireClient.connect(server)) {
            client.start();
            client.sendOversizedFrameHeader();
            int error = client.readProtocolError();
            assertTrue(error == Http2WireClient.FRAME_SIZE_ERROR
                            || error == Http2WireClient.NO_ERROR,
                    () -> "Expected FRAME_SIZE_ERROR or connection close, got " + error);
        }
    }

    @Test
    void resettingOneStreamDoesNotBreakAnotherStream() throws Exception {
        try (Http2WireClient client = Http2WireClient.connect(server)) {
            client.start();
            client.sendGet(1, "/hello");
            client.sendReset(1);
            client.sendGet(3, "/after-reset");

            Map<Integer, String> responses =
                    client.readResponseBodies(Set.of(3), Set.of(1));
            assertEquals("still-connected", responses.get(3));
        }
    }
}
