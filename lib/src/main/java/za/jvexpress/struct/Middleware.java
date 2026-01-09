package za.jvexpress.struct;

import java.util.function.Function;


public abstract class Middleware{
    public abstract void  callback(Request req,Response res,ReqFunction next);
}