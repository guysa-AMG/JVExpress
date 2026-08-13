package za.jvexpress.struct;

/**
 * Immutable record representing an HTTP cookie with name and value.
 * Provides conversion to cookie header format.
 */
public record Cookie(String name,String value){
    /**
     * Converts the cookie to HTTP cookie format (name=value).
     * @return The cookie in Set-Cookie header format
     */
    public String toCookieFormat(){
        return String.format("%s=%s",this.name,this.value);
    }
}