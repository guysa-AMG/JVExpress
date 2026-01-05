# JVExpress
A lightweight, high-performance Java web framework built from the ground up using raw Java Sockets. Designed for developers who want the simplicity of Node's Express.js with the robust multi-threading capabilities of the JVM.
 Features

   <li> Zero Dependencies: Built entirely on standard Java libraries (java.net, java.io, java.util).
</li>
   <li> Low-Level Socket Handling: Manages raw TCP/IP connections and HTTP/1.1 protocol parsing.

</li>    <li>Multi-threaded Architecture: Uses a custom-tuned Thread Pool to handle concurrent requests efficiently.
</li>
   <li> Express-like Routing: Intuitive API for defining GET, POST, and other HTTP methods.
</li>
   <li> JSON Support: Easy integration for building RESTful APIs.
</li>
Architecture

Unlike high-level frameworks that hide the networking layer, this project implements the full lifecycle of a request:

   <li> Acceptance: The ServerSocket listens for incoming byte streams.</li>

  <li>  Dispatch: A WorkerThread is assigned from the pool to prevent blocking the main loop.</li>

  <li>  Parsing: Raw bytes are parsed into a Request object (headers, query params, body).</li>

  <li>  Execution: The router matches the path to a user-defined lambda function.</li>

  <li>  Transmission: The Response object flushes the status code and data back through the socket.

</li>