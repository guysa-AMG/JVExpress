package za.jvexpress.struct;

import java.util.function.Function;

/**
 * Abstract base class for middleware components in the request processing pipeline.
 * Middleware can intercept requests, perform processing, and optionally delegate
 * to the next handler in the chain.
 */
public abstract class Middleware{
    /**
     * Callback method invoked for each request handled by middleware.
     * Implementations can access the request, modify the response, and call next
     * to continue the middleware chain.
     * 
     * @param req The HTTP Request
     * @param res The HTTP Response
     * @param next Function to invoke the next middleware or handler in the chain
     */
    public abstract void  callback(Request req,Response res,ReqFunction next);
}