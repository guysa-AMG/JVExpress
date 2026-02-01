package za.jvexpress.struct;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


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
protected String sendable="";


  



public void send(String data){

this.sendable = data;
}
public void calculate(){
 this.Content_len=this.sendable.length();
}
public String data(){
  return this.sendable;
}

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
private String getCurrentTime(){
DateTimeFormatter format = DateTimeFormatter
                                  .ofPattern("EE, dd MMM yyyy HH:mm:ss 'GMT'",Locale.ENGLISH)
                                  .withZone(ZoneId.of("+2"));
Instant ins = Instant.now();
return format.format(ins);
}
public String get_header(){
   Map<String,String> head= new HashMap();
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
nstring.append("HTTP/1.1 200 OK\r\n");
head.forEach( (k,v)->{
  nstring.append(String.format("%S: %S\r\n", k,v));
} );

System.out.println(nstring.toString());

String response = String.format("""
HTTP/1.1 200 OK
Vary: Origin
Content-Type: text/html

Etag: W/"288-mGR3sQgfYIpy8jQU0NkLVk2Tk3c"

X-Powered-By: JVExpress
Keep-Alive: timeout=5
Content-Length: %S

%S
"""



, this.sendable.length(),this.sendable);//head.toString();


 //response = String.format("%S\n%S %S",head,response,sendable);
return response;
}
 

}