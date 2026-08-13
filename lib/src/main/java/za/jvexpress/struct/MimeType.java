package za.jvexpress.struct;

/**
 * Interface defining standard MIME types supported by the server.
 * Used to set the Content-Type header in HTTP responses.
 */
public interface MimeType {
    String JSON="application/json";
    String HTML="text/html";
    String JS = "text/javascript";
    String CSS = "text/css";
    String PNG = "image/png";
    String JPEG = "image/jpeg";

}
