package za.jvexpress.struct;

import java.util.Optional;

/**
 * Functional interface for handling HTTP requests and generating responses.
 * Used to define route handler callbacks in the server.
 */
@FunctionalInterface
public interface ReqFunction{
  /**
   * Handles an HTTP request and produces a response.
   * 
   * @param req The HTTP Request object
   * @param res The HTTP Response object to populate
   */
  void handle(Request req,Response res);

}