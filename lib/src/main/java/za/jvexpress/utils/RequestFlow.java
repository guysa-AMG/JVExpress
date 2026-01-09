package  za.jvexpress.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import za.jvexpress.struct.DropRouteException;
import za.jvexpress.struct.Middleware;
import za.jvexpress.struct.ReqFunction;
import za.jvexpress.struct.Request;
import za.jvexpress.struct.Response;



public abstract class RequestFlow{

private Map<String,Map<String,ReqFunction>> routes=new HashMap();

private List<Middleware> middlewares = new ArrayList<>();

private Iterator<Middleware> middlerator;
private Log log =new Log();

public void add_Middleware(Middleware middlew){
this.middlewares.add(middlew);

}

public void route(String path,String method,ReqFunction func){this.routes.put(method,Map.of(path, func));};


public void run(Request req,Response res) throws DropRouteException{
    this.middlerator=this.middlewares.iterator();

    String method = req.getMethod();
    String path = req.getPath();

  Map<String,ReqFunction> nx = routes.get(path);
 
  if(nx!=null)
  {
     log.print(path);
    ReqFunction func = nx.get(method);
   if(func !=null)
   log.print(method+" found");
    {
    run_middle(req,res);
    if(!middlerator.hasNext()){func.handle(req, res);}
    else{
      throw new DropRouteException();
    }
    }}

}

public void run_middle(Request req,Response res){
if(middlerator.hasNext()){
    Middleware mid = middlerator.next();
    mid.callback(req, res, this::run_middle);

}

}
}