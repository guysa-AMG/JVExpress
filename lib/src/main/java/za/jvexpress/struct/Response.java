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

 public Response(){
     this.log = new Log();
 }
  

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
public void sendJson(LinkedHashMap data){
String jstring = HashMapParser.toJString(data);
this.Content_Type=MimeType.JSON;
this.sendable=jstring;
}
public void send(String data){

this.sendable = data;
}
public void calculate(){
 this.Content_len=this.sendable.length();
}
public String data(){
  return this.sendable;
}
public void setCookie(String name,String value){
    this.Cookie=String.format("%s=%s",name,value);

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