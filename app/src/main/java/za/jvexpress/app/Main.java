package za.jvexpress.app;


import za.jvexpress.Server;
import za.jvexpress.struct.Cookie;


public class Main{

    
    public static void main(String[] args) {

      Server app = new Server(8089);
      app.init();
  

     app.get("/user",(req, res)->{
      res.send("Hi "+req.getQueries().get("user").get("name"));
     });


      app.get("/",(req, res)->{
      Cookie mec = new Cookie("user","GuySA");
      res.setCookie(mec);
      res.send("Hi bro");
     });


     app.listen();
    }
}