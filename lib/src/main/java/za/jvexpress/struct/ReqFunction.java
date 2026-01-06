package za.jvexpress.struct;

import java.lang.FunctionalInterface;
import java.util.function.Function;

import za.jvexpress.struct.Request;
import za.jvexpress.struct.Response;

@FunctionalInterface
public interface ReqFunction{
  Response handle(Request req,Response res,FunctionalInterface next);
}