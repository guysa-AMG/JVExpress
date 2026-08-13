package za.jvexpress.struct;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import za.jvexpress.tool.HashMapParser;
import za.jvexpress.utils.Log;

/**
 * Represents an HTTP Response object used to build and format HTTP response headers and body.
 * Supports setting response status, content type, cookies, and sending various content types
 * including text, JSON, and files.
 */
public class Response{

protected String head ="HTTP/1.1 200 OK";
protected String Vary= "Origin";
protected String Content_Type= "text/html";
protected String Cache_Control= "no-cache";
protected String Etag= "W/288-mGR3sQgfYIpy8jQU0NkLVk2Tk3c";
protected String Date= "Tue, 06 Jan 2026 14,36,52 GMT";
protected String Connection= "keep-alive";
protected String Keep_Alive= "timeout=5";
protected int Content_len = 0;
protected String Cookie;
protected String sendable="";
protected Log log;

 /**
  * Constructs a Response object with default HTTP 200 OK headers.
  * Initializes content type, cache control, and other standard HTTP headers.
  */
 public Response(){
     this.log = new Log();
 }
  
/**
 * Reads a file from the classpath and sets it as the response body.
 * Looks for files in the za/jvexpress/app/template/ directory.
 * 
 * @param filename The name of the file to send
 */
public void sendFile(String filename)
{

    String package_dir =String.format("za/jvexpress/app/template/%s",filename);
   ;
    InputStream inpf = Response.class.getClassLoader().getResourceAsStream(package_dir);
    System.out.println(inpf);
    StringBuilder str_builder = new StringBuilder();

     try{
         int data = -1;
         if (inpf != null) {
             data = inpf.read();
         }
         while (data != -1) {
             str_builder.append((char)data);
            data = inpf.read();
        }
    }catch (IOException except){
        log.eprint(except);
    }
     this.sendable=str_builder.toString();
}

/**
 * Converts a LinkedHashMap to JSON string format and sets as the response body.
 * Automatically sets Content-Type to application/json.
 * 
 * @param data The map to convert to JSON
 */
public void sendJson(LinkedHashMap data){
String jstring = HashMapParser.toJString(data);
this.Content_Type=MimeType.JSON;
this.sendable=jstring;
}

/**
 * Sets the response body to a plain text string.
 * 
 * @param data The text content to send
 */
public void send(String data){

this.sendable = data;
}

/**
 * Calculates the Content-Length header based on the sendable data.
 */
public void calculate(){
 this.Content_len=this.sendable.length();
}

/**
 * Gets the response body data.
 * @return The sendable content
 */
public String data(){
  return this.sendable;
}

/**
 * Sets a cookie in the response by adding it to the Set-Cookie header.
 * 
 * @param cookie The Cookie to set
 */
public void setCookie(Cookie cookie){

    this.Cookie=cookie.toCookieFormat();

}

/**
 * Generates a 404 Not Found HTTP response.
 * @return The complete 404 response string with headers
 */
public String get_not_found(){

  String response = String.format("""
HTTP/1.1 404 Not Found
Vary: Origin
Content-Type: text/html
Cache-Control: no-cache
Date: Tue, 06 Jan 2026 14:36:52 GMT
Connection: keep-alive
X-Powered-By: JVExpress
Keep-Alive: timeout=5

"""

, this.sendable.length(),this.sendable);//head.toString();


return response;
}

/**
 * Gets the current time formatted as an HTTP date string (RFC 7231 format).
 * @return Formatted date string in timezone +2
 */
private String getCurrentTime(){

DateTimeFormatter format = DateTimeFormatter
                                  .ofPattern("EE, dd MMM yyyy HH:mm:ss 'GMT'",Locale.ENGLISH)
                                  .withZone(ZoneId.of("+2"));
Instant ins = Instant.now();
return format.format(ins);
}

/**
 * Builds the complete HTTP response including headers and body.
 * Calculates Content-Length, adds all headers, and appends the response body.
 * Includes Set-Cookie header if a cookie was set.
 * 
 * @return The complete HTTP response string ready to send
 */
public String get_header(){
   Map<String,String> head= new LinkedHashMap<>();
  StringBuilder nstring=new StringBuilder();
  this.calculate();
head.put("Vary", Vary);
head.put("Content-Type", Content_Type);
head.put("Cache-Control", Cache_Control);
head.put("Etag", Etag);
head.put("Date",getCurrentTime());
head.put("Connection", Connection);
head.put("Keep-Alive", Keep_Alive);
head.put("Server", "JVExpress");
head.put("Content-Length", String.valueOf(Content_len));
if (this.Cookie != null){
head.put("Set-Cookie",this.Cookie);
}
nstring.append("HTTP/1.1 200 OK\r\n");
head.forEach( (k,v)->{
  nstring.append(String.format("%s: %s\r\n", k,v));
} );
nstring.append("\r\n");

nstring.append(this.sendable);


 //response = String.format("%S\n%S %S",head,response,sendable);
    System.out.print(nstring.toString());
return nstring.toString();
}
 

}