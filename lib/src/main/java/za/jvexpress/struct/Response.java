package za.jvexpress.struct;

import java.util.HashMap;
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
protected int Content_len = 648;
protected String sendable="""
<!doctype html>
<html lang="en">
  <head>
    <script type="module">import { injectIntoGlobalHook } from "/@react-refresh";
injectIntoGlobalHook(window);
window.$RefreshReg$ = () => {};
window.$RefreshSig$ = () => (type) => type;</script>

    <script type="module" src="/@vite/client"></script>

    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/vite.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>SASL Chatbot - WeThinkCode Cohort 2025</title>
    
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>

""";


  



public void send(String data){

this.sendable = data;
}
public void calculate(){
 this.Content_len=this.sendable.length();
}
public String get_header(){
   Map<String,String> head= new HashMap();
  
  this.calculate();
head.put("Vary", Vary);
head.put("Content-Type", Content_Type);
head.put("Cache-Control", Cache_Control);
head.put("Etag", Etag);
head.put("Date",Date);
head.put("Connection", Connection);
head.put("Keep-Alive", Keep_Alive);
head.put("Content-Length", String.valueOf(Content_len));

String response = """
HTTP/1.1 200 OK
Vary: Origin
Content-Type: text/html
Cache-Control: no-cache
Etag: W/"288-mGR3sQgfYIpy8jQU0NkLVk2Tk3c"
Date: Tue, 06 Jan 2026 14:36:52 GMT
Connection: keep-alive
Keep-Alive: timeout=5
Content-Length: 2

Hi
""";//head.toString();


 //response = String.format("%S\n%S %S",head,response,sendable);
return response;
}
 

}