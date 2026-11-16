package za.jvexpress.test;

import java.net.InetSocketAddress;
import java.util.Map;
import javax.net.ssl.SSLContext;

/**
 * Test-only seam for starting the server through its eventual public HTTPS API.
 * Implementations are registered with {@link java.util.ServiceLoader}.
 */
public interface Http2ServerAdapter {

    RunningServer start(SSLContext serverTlsContext, Map<String, String> getRoutes) throws Exception;

    interface RunningServer extends AutoCloseable {
        InetSocketAddress address();

        @Override
        void close() throws Exception;
    }
}
