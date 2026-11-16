package za.jvexpress.test;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;

final class Http2WireClient implements AutoCloseable {

    static final int NO_ERROR = 0;
    static final int PROTOCOL_ERROR = 1;
    static final int FRAME_SIZE_ERROR = 6;

    private static final byte[] CLIENT_PREFACE =
            "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(StandardCharsets.US_ASCII);
    private static final int DATA = 0;
    private static final int HEADERS = 1;
    private static final int RST_STREAM = 3;
    private static final int SETTINGS = 4;
    private static final int PING = 6;
    private static final int GOAWAY = 7;
    private static final int WINDOW_UPDATE = 8;
    private static final int CONTINUATION = 9;
    private static final int FLAG_END_STREAM = 0x1;
    private static final int FLAG_ACK = 0x1;
    private static final int FLAG_END_HEADERS = 0x4;

    private final SSLSocket socket;
    private final InputStream input;
    private final OutputStream output;
    private final String authority;
    private int peerMaxFrameSize = 16_384;

    private Http2WireClient(SSLSocket socket, String authority) throws IOException {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
        this.authority = authority;
    }

    static Http2WireClient connect(Http2TestServer server) throws Exception {
        SSLSocket socket = (SSLSocket) server.clientTlsContext()
                .getSocketFactory()
                .createSocket("localhost", server.address().getPort());
        socket.setSoTimeout(2_000);
        SSLParameters parameters = socket.getSSLParameters();
        parameters.setProtocols(new String[] { "TLSv1.2", "TLSv1.3" });
        parameters.setApplicationProtocols(new String[] { "h2" });
        socket.setSSLParameters(parameters);
        socket.startHandshake();
        if (!"h2".equals(socket.getApplicationProtocol())) {
            socket.close();
            throw new IOException("Expected ALPN h2 but negotiated "
                    + socket.getApplicationProtocol());
        }
        return new Http2WireClient(socket, server.authority());
    }

    void start() throws IOException {
        output.write(CLIENT_PREFACE);
        writeFrame(SETTINGS, 0, 0, new byte[0]);
        output.flush();

        boolean settingsReceived = false;
        while (!settingsReceived) {
            Frame frame = readFrame();
            if (frame.type() == SETTINGS && frame.streamId() == 0) {
                if ((frame.flags() & FLAG_ACK) == 0) {
                    readSettings(frame.payload());
                    writeFrame(SETTINGS, FLAG_ACK, 0, new byte[0]);
                    output.flush();
                    settingsReceived = true;
                }
            } else if (frame.type() == PING && (frame.flags() & FLAG_ACK) == 0) {
                writeFrame(PING, FLAG_ACK, 0, frame.payload());
                output.flush();
            }
        }
    }

    void sendGet(int streamId, String path) throws IOException {
        sendRequest(streamId, path, false);
    }

    void writeBytes(byte[] bytes) throws IOException {
        output.write(bytes);
        output.flush();
    }

    void sendRequestWithConnectionHeader(int streamId, String path) throws IOException {
        sendRequest(streamId, path, true);
    }

    private void sendRequest(int streamId, String path, boolean connectionHeader)
            throws IOException {
        ByteArrayOutputStream headers = new ByteArrayOutputStream();
        headers.write(0x82); // :method GET
        headers.write(0x87); // :scheme https
        writeInteger(headers, 4, 4); // :path
        writeString(headers, path);
        writeInteger(headers, 4, 1); // :authority
        writeString(headers, authority);
        if (connectionHeader) {
            headers.write(0x00); // literal header with a literal name
            writeString(headers, "connection");
            writeString(headers, "keep-alive");
        }
        writeFrame(HEADERS, FLAG_END_HEADERS | FLAG_END_STREAM,
                streamId, headers.toByteArray());
        output.flush();
    }

    void sendZeroWindowUpdate() throws IOException {
        writeFrame(WINDOW_UPDATE, 0, 0, new byte[4]);
        output.flush();
    }

    void sendDataOnIdleStream(int streamId) throws IOException {
        writeFrame(DATA, 0, streamId, new byte[] { 1 });
        output.flush();
    }

    void sendOversizedFrameHeader() throws IOException {
        if (peerMaxFrameSize == 0x00ff_ffff) {
            throw new IllegalStateException("Peer permits the maximum HTTP/2 frame size");
        }
        writeFrameHeader(peerMaxFrameSize + 1, DATA, 0, 1);
        output.flush();
    }

    void sendReset(int streamId) throws IOException {
        ByteArrayOutputStream payload = new ByteArrayOutputStream(4);
        writeInt(payload, 8); // CANCEL
        writeFrame(RST_STREAM, 0, streamId, payload.toByteArray());
        output.flush();
    }

    Map<Integer, String> readResponseBodies(Set<Integer> streamIds) throws IOException {
        return readResponseBodies(streamIds, Set.of());
    }

    Map<Integer, String> readResponseBodies(
            Set<Integer> streamIds, Set<Integer> ignoredStreamIds) throws IOException {
        Map<Integer, MutableResponse> responses = new LinkedHashMap<>();
        for (int streamId : streamIds) {
            responses.put(streamId, new MutableResponse());
        }

        while (responses.values().stream().anyMatch(response -> !response.ended)) {
            Frame frame = readFrame();
            switch (frame.type()) {
                case SETTINGS -> {
                    if ((frame.flags() & FLAG_ACK) == 0) {
                        readSettings(frame.payload());
                        writeFrame(SETTINGS, FLAG_ACK, 0, new byte[0]);
                        output.flush();
                    }
                }
                case PING -> {
                    if ((frame.flags() & FLAG_ACK) == 0) {
                        writeFrame(PING, FLAG_ACK, 0, frame.payload());
                        output.flush();
                    }
                }
                case HEADERS -> {
                    MutableResponse response = responseForFrame(
                            responses, ignoredStreamIds, frame.streamId());
                    if (response == null) {
                        if ((frame.flags() & FLAG_END_HEADERS) == 0) {
                            readContinuationFrames(frame.streamId());
                        }
                        continue;
                    }
                    response.headersReceived = true;
                    if ((frame.flags() & FLAG_END_HEADERS) == 0) {
                        readContinuationFrames(frame.streamId());
                    }
                    response.ended = (frame.flags() & FLAG_END_STREAM) != 0;
                }
                case DATA -> {
                    MutableResponse response = responseForFrame(
                            responses, ignoredStreamIds, frame.streamId());
                    if (response != null) {
                        response.body.write(frame.payload());
                        response.ended = (frame.flags() & FLAG_END_STREAM) != 0;
                    }
                }
                case RST_STREAM -> throw new IOException(
                        "Server reset stream " + frame.streamId());
                case GOAWAY -> throw new IOException("Server sent GOAWAY");
                default -> {
                    // Ignore connection-control frames not needed by these tests.
                }
            }
        }

        Map<Integer, String> bodies = new LinkedHashMap<>();
        responses.forEach((streamId, response) -> {
            if (!response.headersReceived) {
                throw new IllegalStateException("No response headers for stream " + streamId);
            }
            bodies.put(streamId, response.body.toString(StandardCharsets.UTF_8));
        });
        return bodies;
    }

    int readProtocolError() throws IOException {
        try {
            while (true) {
                Frame frame = readFrame();
                if (frame.type() == RST_STREAM && frame.payload().length >= 4) {
                    return readInt(frame.payload(), 0);
                }
                if (frame.type() == GOAWAY && frame.payload().length >= 8) {
                    return readInt(frame.payload(), 4);
                }
                if (frame.type() == SETTINGS && (frame.flags() & FLAG_ACK) == 0) {
                    writeFrame(SETTINGS, FLAG_ACK, 0, new byte[0]);
                    output.flush();
                }
            }
        } catch (SocketTimeoutException timeout) {
            throw new IOException("Server neither rejected the invalid input nor closed", timeout);
        } catch (EOFException | SSLException | SocketException closed) {
            return NO_ERROR;
        }
    }

    private void readContinuationFrames(int streamId) throws IOException {
        int flags;
        do {
            Frame continuation = readFrame();
            if (continuation.type() != CONTINUATION
                    || continuation.streamId() != streamId) {
                throw new IOException("Invalid response CONTINUATION sequence");
            }
            flags = continuation.flags();
        } while ((flags & FLAG_END_HEADERS) == 0);
    }

    private static MutableResponse responseForFrame(
            Map<Integer, MutableResponse> responses,
            Set<Integer> ignoredStreamIds,
            int streamId) throws IOException {
        MutableResponse response = responses.get(streamId);
        if (response == null && !ignoredStreamIds.contains(streamId)) {
            throw new IOException("Response received for unexpected stream " + streamId);
        }
        return response;
    }

    private void readSettings(byte[] payload) throws IOException {
        if (payload.length % 6 != 0) {
            throw new IOException("Malformed SETTINGS payload");
        }
        for (int offset = 0; offset < payload.length; offset += 6) {
            int identifier = ((payload[offset] & 0xff) << 8)
                    | (payload[offset + 1] & 0xff);
            if (identifier == 5) {
                int value = readInt(payload, offset + 2);
                if (value < 16_384 || value > 0x00ff_ffff) {
                    throw new IOException("Invalid SETTINGS_MAX_FRAME_SIZE: " + value);
                }
                peerMaxFrameSize = value;
            }
        }
    }

    private Frame readFrame() throws IOException {
        byte[] header = input.readNBytes(9);
        if (header.length == 0) {
            throw new EOFException("Peer closed the HTTP/2 connection");
        }
        if (header.length != 9) {
            throw new EOFException("Truncated HTTP/2 frame header");
        }
        int length = ((header[0] & 0xff) << 16)
                | ((header[1] & 0xff) << 8)
                | (header[2] & 0xff);
        int type = header[3] & 0xff;
        int flags = header[4] & 0xff;
        int streamId = readInt(header, 5) & 0x7fff_ffff;
        byte[] payload = input.readNBytes(length);
        if (payload.length != length) {
            throw new EOFException("Truncated HTTP/2 frame payload");
        }
        return new Frame(type, flags, streamId, payload);
    }

    private void writeFrame(int type, int flags, int streamId, byte[] payload)
            throws IOException {
        writeFrameHeader(payload.length, type, flags, streamId);
        output.write(payload);
    }

    private void writeFrameHeader(int length, int type, int flags, int streamId)
            throws IOException {
        output.write((length >>> 16) & 0xff);
        output.write((length >>> 8) & 0xff);
        output.write(length & 0xff);
        output.write(type & 0xff);
        output.write(flags & 0xff);
        writeInt(output, streamId & 0x7fff_ffff);
    }

    private static void writeInteger(ByteArrayOutputStream output, int prefixBits, int value) {
        int prefixMax = (1 << prefixBits) - 1;
        if (value < prefixMax) {
            output.write(value);
            return;
        }
        output.write(prefixMax);
        value -= prefixMax;
        while (value >= 128) {
            output.write((value & 0x7f) | 0x80);
            value >>>= 7;
        }
        output.write(value);
    }

    private static void writeString(ByteArrayOutputStream output, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeInteger(output, 7, bytes.length);
        output.writeBytes(bytes);
    }

    private static void writeInt(OutputStream output, int value) throws IOException {
        output.write((value >>> 24) & 0xff);
        output.write((value >>> 16) & 0xff);
        output.write((value >>> 8) & 0xff);
        output.write(value & 0xff);
    }

    private static int readInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xff) << 24)
                | ((bytes[offset + 1] & 0xff) << 16)
                | ((bytes[offset + 2] & 0xff) << 8)
                | (bytes[offset + 3] & 0xff);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    private record Frame(int type, int flags, int streamId, byte[] payload) {
    }

    private static final class MutableResponse {
        private final ByteArrayOutputStream body = new ByteArrayOutputStream();
        private boolean headersReceived;
        private boolean ended;
    }
}
