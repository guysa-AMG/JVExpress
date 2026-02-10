package za.jvexpress.app;


import java.util.LinkedHashMap;
import java.util.Map;

import za.jvexpress.Server;


public class Main{

    
    public static void main(String[] args) {

      Server app = new Server(8089);
      app.init();
      LinkedHashMap data = new LinkedHashMap();
      data.put("user","student");
      data.put("email","amguysa7@gmail.com");
      data.put("id","9035");
      Map hobby = new LinkedHashMap();
      hobby.put("sport", "rugby");
      hobby.put("player", "me");
      data.put("hobby", hobby);

        LinkedHashMap m = new LinkedHashMap();
        m.put("user","student");
        m.put("email","amguysa7@gmail.com");
        m.put("id","9035");
        Map b = new LinkedHashMap();
        b.put("sport", "rugby");
        b.put("player", "me");
        m.put("hobby", b);
        data.put("some",m);

     app.get("/",(req, res)->{
       res.sendJson(data);
    //res.send("Hi bro");
     });

     app.listen();
    }
}