package za.jvexpress.app;


import za.jvexpress.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class Main{

    
    public static void main(String[] args) {

      Server app = new Server(8080);
      app.init();
      Map data = new TreeMap();
      data.put("user","student");
      data.put("email","amguysa7@gmail.com");
      data.put("id","9035");


     app.get("/",(req, res)->{
       res.sendJson(data);
    //res.send("Hi bro");
     });

     app.listen();
    }
}