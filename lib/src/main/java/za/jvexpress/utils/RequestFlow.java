package  za.jvexpress.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import za.jvexpress.struct.DropRouteException;
import za.jvexpress.struct.Middleware;
import za.jvexpress.struct.ReqFunction;
import za.jvexpress.struct.Request;
import za.jvexpress.struct.Response;

/**
 * Abstract base class for handling HTTP request routing and middleware execution.
 * Manages the registration of routes for different HTTP methods and maintains a middleware chain.
 * Executes middleware in sequence and delegates to route handlers.
 */
public abstract class RequestFlow{

private Map<String,Map<String,ReqFunction>> routes=new HashMap();

private List<Middleware> middlewares = new ArrayList<>();

private Iterator<Middleware> middlerator;
private Log log =new Log();

/**
 * Adds a middleware to the middleware chain.
 * Middleware is executed in the order it is added.
 * 
 * @param middlew The middleware to add
 */
public void add_Middleware(Middleware middlew){
this.middlewares.add(middlew);

}

/**
 * Registers a route handler for a specific HTTP method and path.
 * 
 * @param method The HTTP method (GET, POST, etc.)
 * @param path The URL path
 * @param func The handler function
 */
public void route(String method,String path,ReqFunction func){this.routes.put(method,Map.of(path, func));};

/**
 * Executes the request processing pipeline: middleware chain, then route handler.
 * If middleware processing is not complete, throws DropRouteException.
 * Handles static file serving for /public/ paths.
 * 
 * @param req The HTTP Request
 * @param res The HTTP Response
 * @throws DropRouteException if no route is found
 */
public void run(Request req,Response res) throws DropRouteException{
    this.middlerator=this.middlewares.iterator();

    String method = req.getMethod();
    String path = req.getPath();

  Map<String,ReqFunction> nx = routes.get(method);
 
  if(nx!=null)
  {
     log.print(routes.toString());
      log.print(req.toString());
    ReqFunction func = nx.get(path);
   if(func !=null)
    {
    run_middle(req,res);
    if(!middlerator.hasNext()){
        func.handle(req, res);}
    else{
      throw new DropRouteException();
    }
    }}else{
     if(path.startsWith("/public/")){
         res.sendFile(path);
     }
        else{
          throw new DropRouteException();
      }
    }

  

}

/**
 * Recursively executes the middleware chain.
 * Retrieves the next middleware and invokes its callback, allowing it to
 * optionally continue to the next middleware in the chain.
 * 
 * @param req The HTTP Request
 * @param res The HTTP Response
 */
public void run_middle(Request req,Response res){
if(middlerator.hasNext()){
    Middleware mid = middlerator.next();
    mid.callback(req, res, this::run_middle);

}

}
}