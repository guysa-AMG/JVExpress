package za.jvexpress.struct;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an HTTP Request parsed from raw HTTP protocol data.
 * Parses the HTTP request line and headers to extract method, path, host, cookies,
 * authorization tokens, and query parameters from the URL.
 */
public class Request{
private final  String rawHeader;
protected  String data;
protected String host;
protected String user_agent;
protected String accept;
protected String  accept_lang;
protected String accept_encoding;
protected String cookie;
protected String upgrade_insecure_request;
protected String connection;
protected Map<String,Map<String,?>> queries;
protected String sec_fetch_dest;
protected String sec_fetch_mode;
protected String sec_fetch_site;
protected String sec_fetch_user;
protected String priority;
protected String method;
protected String authorization;
protected String cache_control;
protected String path;
protected  String http_version;



    /**
     * Constructs a Request with parsed HTTP header information.
     * 
     * @param path The URL path extracted from the request line
     * @param raw The raw HTTP request data
     * @param host The Host header value
     * @param method The HTTP method (GET, POST, etc.)
     * @param cookie The Cookie header value
     * @param uir The Upgrade-Insecure-Requests header value
     * @param user_agent The User-Agent header value
     * @param connection The Connection header value
     * @param accept The Accept header value
     * @param accept_lang The Accept-Language header value
     * @param authorization The Authorization header value
     * @param httpV The HTTP version (e.g., HTTP/1.1)
     * @param queries Map of parsed query parameters from the URL
     */
    public Request(
        String path,
        String raw,
        String host,
        String method,
        String cookie,
        String uir,
        String user_agent,
        String connection,
        String accept,
        String accept_lang,
        String authorization,
        String httpV,
        Map<String,Map<String,?>> queries
               
    ) {
        this.path=path;
        this.http_version=httpV;
        this.accept = accept;
        this.rawHeader = raw;
        this.connection = connection;
        this.accept_lang = accept_lang;
        this.upgrade_insecure_request = uir;
        this.user_agent = user_agent;
        this.host = host;
        this.cookie=cookie;
        this.method = method;
        this.authorization = authorization;
        this.queries = queries;
    }


/**
 * Parses raw HTTP request data into a Request object.
 * Extracts the HTTP method, path, version from the request line.
 * Parses headers into key-value pairs.
 * Extracts and parses query parameters from the URL path.
 * Supports single and multiple query parameters with key=value pairs and ampersand-separated values.
 * 
 * @param data The raw HTTP request string
 * @return A new Request object with parsed information
 */
public static Request fromData(String data){
    String[] lines =data.split("\r\n");
    Map<String,String> collection =new TreeMap<>();
    String method = lines[0].split(" ")[0];
    String path = lines[0].split(" ")[1];
    String httpVersion = lines[0].split(" ")[2];
    for(String line : lines){
    String[] dx =line.split(":",2);
    if (dx.length>1){
    collection.put(dx[0].toLowerCase(),dx[1].trim());
    }
    }
    Map<String,Map<String,?>> queries = new HashMap<>();
   for (String index:path.split("/")){
   
    if (index.contains("?")){
        
        
        String[] routeQuStrings = index.split("\\?");
       path = "/"+routeQuStrings[0];
        if (routeQuStrings[1].contains("&")){
              Map<String,String> singleQuerie=null; 
              singleQuerie = new HashMap<>();
            for(String que:routeQuStrings[1].split("&")){
                if (que.contains("=")){

                String[] singleData =que.split("=");
               
                singleQuerie.put(singleData[0], singleData[1]);
             }
             else{
                singleQuerie.put(que,null);
             }
             
            }
             queries.put(routeQuStrings[0],singleQuerie);

        }
        else{


            Map<String,String> singleQuerie=null;
            if (index.contains("=")){

                String[] singleData =routeQuStrings[1].split("=");
                singleQuerie = new HashMap<>();
                singleQuerie.put(singleData[0], singleData[1]);
             }
             System.out.printf("%s : %s\n", routeQuStrings[0],singleQuerie);
             queries.put(routeQuStrings[0],singleQuerie);

        }


    }
   }
 
    return new Request(
        path,
        data,
        collection.get("host"),
        method,
        collection.get("cookie"),
        collection.get("upgrade-insecure-requests"),
        collection.get("user-agent"),
        collection.get("connection"),
        collection.get("accept"),
        collection.get("accept-Language"),
        collection.getOrDefault("authorization","cannot find token"),
        httpVersion,
        queries
           
    );
   
}
 /**
  * Gets the URL path of this request.
  * @return The request path
  */
 public String getPath(){return this.path; }
 
 /**
  * Gets the parsed query parameters from the URL.
  * @return Map of query parameters with nested key-value pairs
  */
public Map<String,Map<String,?>> getQueries(){
    return this.queries;
}
 
 /**
  * Gets the HTTP method of this request.
  * @return The HTTP method (GET, POST, etc.)
  */
 public String getMethod(){return this.method; }
 
 /**
  * Gets the Authorization header value.
  * @return The authorization token or header
  */
public String getAuthorization(){return this.authorization;};
 
 /**
  * Gets the Connection header value.
  * @return The connection type (e.g., keep-alive)
  */
 public String getConnection(){return this.connection; }
 
 /**
  * Gets the User-Agent header value.
  * @return The user agent string
  */
 public String getUserAgent(){return this.user_agent; }
 
 /**
  * Gets the Cookie header value.
  * @return The cookie string
  */
 public String getCookie(){return this.cookie; }

 /**
  * Gets the raw HTTP request data.
  * @return The complete raw HTTP request
  */
  public String getRawHeader(){return this.rawHeader; }

}