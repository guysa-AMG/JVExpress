# JVExpress: A Lightweight Java Web Framework

JVExpress is a minimalist, high-performance web framework for Java, built from the ground up using raw Java Sockets. It is designed for developers who appreciate the simplicity of Node.js's Express.js but want to leverage the robust multi-threading capabilities of the Java Virtual Machine (JVM).

## Why JVExpress?

In a world of powerful but often complex frameworks like Spring Boot, JVExpress offers a refreshing alternative. It is designed to be lightweight and intuitive, making it an excellent choice for:

*   **Beginners:** If you're new to web development in Java, JVExpress provides a gentle learning curve without overwhelming you with abstractions.
*   **Small to Medium-Sized Projects:** For projects that don't require the full might of a large framework, JVExpress offers a nimble and efficient solution.
*   **Learning and Experimentation:** By working closer to the underlying networking layer, you'll gain a deeper understanding of how web servers work.

## Features

*   **Zero Dependencies:** Built entirely on standard Java libraries (`java.net`, `java.io`, `java.util`), which means no external JARs to manage.
*   **Low-Level Socket Handling:** Manages raw TCP/IP connections and parses HTTP/1.1 protocol messages, giving you a transparent view of the request-response cycle.
*   **Multi-threaded Architecture:** Utilizes a custom-tuned thread pool to handle concurrent requests efficiently, ensuring your application remains responsive under load.
*   **Express-like Routing:** If you're coming from a Node.js background, you'll feel right at home with the intuitive API for defining `GET`, `POST`, and other HTTP methods.
*   **JSON Support:** Easily build RESTful APIs with built-in support for JSON, making it a great choice for modern web services.

## Getting Started

Here's a simple "Hello World" example to get you up and running with JVExpress:

```java
import za.jvexpress.Server;
import za.jvexpress.struct.Request;
import za.jvexpress.struct.Response;

public class Main {
    public static void main(String[] args) {

        Server app = new Server(8080);

        app.get("/", (Request req, Response res) -> {
     
            res.send("Hello, World!");
        });

        app.listen();
    }
}
```

## Architecture

Unlike high-level frameworks that hide the networking layer, JVExpress implements the full lifecycle of a request:

1.  **Acceptance:** The `ServerSocket` listens for incoming byte streams from clients.
2.  **Dispatch:** A `WorkerThread` is assigned from a pool to handle the request, preventing the main loop from blocking.
3.  **Parsing:** The raw bytes are parsed into a `Request` object, which includes headers, query parameters, and the request body.
4.  **Execution:** The router matches the request's path to a user-defined lambda function.
5.  **Transmission:** The `Response` object flushes the status code, headers, and data back to the client through the socket.

## How to Contribute

We welcome contributions from the community! If you'd like to get involved, please feel free to:

*   **Report bugs:** If you find a bug, please open an issue on our GitHub repository.
*   **Suggest features:** Have an idea for a new feature? We'd love to hear it!
*   **Submit pull requests:** If you've fixed a bug or implemented a new feature, we encourage you to submit a pull request.

## License

JVExpress is open-source software licensed under the [MIT License](LICENSE).