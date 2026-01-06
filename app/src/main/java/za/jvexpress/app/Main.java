package za.jvexpress.app;


import java.util.function.Function;

import za.jvexpress.Server;
import za.jvexpress.struct.Request;
import za.jvexpress.struct.Response;
import za.jvexpress.struct.ReqFunction;
public class Main{
    
    public static void main(String[] args) {
        
      Server app = new Server(8080);
      app.init();
      
     
     app.get("/",(Request req,Response res,Function middle )->{
      return res;
     });

     app.listen();
    }
}