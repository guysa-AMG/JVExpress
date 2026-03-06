package za.jvexpress.middleware;
import za.jvexpress.struct.Middleware;
import za.jvexpress.struct.ReqFunction;
import za.jvexpress.struct.Request;
import za.jvexpress.struct.Response;
import za.jvexpress.utils.Log;

public class Jwtoken extends Middleware {

    @Override
    public void callback(Request req, Response res, ReqFunction next) {
      if(req.getAuthorization()==null){

        (new Log()).libprint("No token found",Log.LogLevel.ERROR);
      }
    }
    
}
