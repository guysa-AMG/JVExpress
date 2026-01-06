package za.jvexpress.struct;

import java.util.HashMap;
import java.util.Map;


public class Response{
private final String rawHeader;
protected  String data;
protected String host;
protected String user_agent;
protected String accept;
protected String  accept_lang;
protected String accept_encoding;
protected String cookie;
protected String upgrade_insecure_request;
protected String connection;
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



    public Response(
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
        String authorazation,
        String httpV
               
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
        this.method = method;
        this.authorization = authorazation;
    }


public static Response fromContext(Request data){
   
    
    return  data;
   
}
 public String getPath(){return this.path; }

 public String getMethod(){return this.method; }

 public String getConnection(){return this.connection; }
 
 public String getUserAgent(){return this.user_agent; }
 
 public String getCookie(){return this.cookie; }

 public String getRawHeader(){return this.rawHeader; }


}