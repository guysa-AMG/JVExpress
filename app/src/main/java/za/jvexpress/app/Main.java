package za.jvexpress.app;


import za.jvexpress.Server;
import za.jvexpress.struct.Cookie;


public class Main{

    
    public static void main(String[] args) {

      Server app = new Server(8089);
      app.init();
  

     app.get("http://example.com",(req, res)->{
      Cookie mec = new Cookie("user","GuySA");
      res.setCookie(mec);
  
    res.send("Hi bro");
     });
      app.get("http://example.com/user?name=john&password=mark",(req, res)->{
      Cookie mec = new Cookie("user","GuySA");
      res.setCookie(mec);
  
    res.send("Hi bro");
     });
     app.listen();
    }
}