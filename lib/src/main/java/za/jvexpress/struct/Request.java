package za.jvexpress.struct;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


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
protected Map<String,?> queries;
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
        Map<String,?> queries
               
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
        this.authorization = authorization;
        this.queries = queries;
    }


public static Request fromData(String data){
    String[] lines =data.split("\r\n");
    Map<String,String> collection =new TreeMap<>();
    String method = lines[0].split(" ")[0];
    String path = lines[0].split(" ")[1];
    String httpVersion = lines[0].split(" ")[2];
    for(String line : lines){
    String[] dx =line.split(":");
    if (dx.length>1){
    collection.put(dx[0],dx[1]);
    }
    }
    Map<String,Map<String,String>> queries = new HashMap<>();
   for (String index:path.split("/")){
   
    if (index.contains("?")){
        
        
        String[] routeQuStrings = index.split("\\?");
       
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
        collection.get("Host"),
        method,
        collection.get("Cookie"),
        collection.get("Upgrade-Insecure-Requests"),
        collection.get("User-Agent"),
        collection.get("Connection"),
        collection.get("Accept"),
        collection.get("Accept-Language"),
        collection.get("Authorization"),
        httpVersion,
        queries
           
    );
   
}
 public String getPath(){return this.path; }

 public String getMethod(){return this.method; }
public String getAuthorization(){return this.authorization;};
 public String getConnection(){return this.connection; }
 
 public String getUserAgent(){return this.user_agent; }
 
 public String getCookie(){return this.cookie; }

  public String getRawHeader(){return this.rawHeader; }

}