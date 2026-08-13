package za.jvexpress.middleware;
import za.jvexpress.struct.Middleware;
import za.jvexpress.struct.ReqFunction;
import za.jvexpress.struct.Request;
import za.jvexpress.struct.Response;
import za.jvexpress.utils.Log;

/**
 * Middleware for JWT (JSON Web Token) authentication.
 * Checks if an Authorization header is present in the request.
 * Logs an error if no token is found, but does not block the request from proceeding.
 */
public class Jwtoken extends Middleware {

    /**
     * Validates the presence of an Authorization header in the request.
     * Logs an error message if the authorization header is missing or null.
     * 
     * @param req The HTTP Request to check for authorization
     * @param res The HTTP Response (not used in this middleware)
     * @param next The next middleware function (not invoked)
     */
    @Override
    public void callback(Request req, Response res, ReqFunction next) {
      if(req.getAuthorization()==null){

        (new Log()).libprint("No token found",Log.LogLevel.ERROR);
      }
    }
    
}
