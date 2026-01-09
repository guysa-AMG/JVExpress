package za.jvexpress.struct;

import java.util.Optional;

@FunctionalInterface
public interface ReqFunction{
  
  void handle(Request req,Response res);

}