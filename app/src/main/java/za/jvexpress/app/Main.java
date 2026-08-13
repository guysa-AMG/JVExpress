package za.jvexpress.app;


import za.jvexpress.Server;
import za.jvexpress.struct.Cookie;

/**
 * Main entry point for the JVExpress web server application.
 * Initializes and configures a basic HTTP server with route handlers
 * and demonstrates cookie and query parameter handling.
 */
public class Main{

    /**
     * Main method that sets up and starts the JVExpress web server.
     * Configures two GET routes:
     * - /user: Responds with a greeting including the "name" query parameter
     * - /: Sets a cookie and returns a simple message
     * 
     * @param args Command line arguments (not used)
     */
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