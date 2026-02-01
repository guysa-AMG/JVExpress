package za.jvexpress.app;


import za.jvexpress.Server;

public class Main{

    
    public static void main(String[] args) {



        Server app = new Server(8080);
      app.init();
      
     app.get("/",(req, res)->{
   res.sendFile("index.html");

    
     });
  

     app.listen();
    }
}