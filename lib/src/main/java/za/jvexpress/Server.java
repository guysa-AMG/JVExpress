package za.jvexpress;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import za.jvexpress.struct.DropRouteException;
import za.jvexpress.struct.Middleware;
import za.jvexpress.struct.ReqFunction;
import za.jvexpress.struct.Request;
import za.jvexpress.struct.Response;
import za.jvexpress.utils.Log;
import za.jvexpress.utils.Log.LogLevel;
import za.jvexpress.utils.RequestContext;
import za.jvexpress.utils.RequestFlow;

/**
 * HTTP server implementation using NIO (Non-blocking I/O) with a selector-based architecture.
 * Extends RequestFlow to support HTTP routing and middleware processing.
 * Handles concurrent connections using Java NIO Selector for scalable event-driven handling.
 * Supports HTTP methods: GET, POST, PATCH, PUT, DELETE.
 */

public  class Server extends RequestFlow {
    private ServerSocketChannel sock; 
    private Selector selector;
    private Log log;
   
    private Boolean should_close_after_one_request=false;
   
    private int port;
    
    /**
     * Constructs a Server with a specified port.
     * Initializes the ServerSocketChannel and Selector for NIO operations.
     * Sets up a shutdown hook to gracefully close resources on application exit.
     * 
     * @param qport The port number to bind the server to. Defaults to 8080 if 0 is provided.
     */
    public Server(int qport) {
        
        if(qport==0){port =8080; }
        else{ port =qport; }
        port = qport;
    try {
        sock =  ServerSocketChannel.open();
         selector=  Selector.open();
         
    } 
    catch (IOException e) { log.eprint(e); }

      log = new Log();
      Runtime.getRuntime().addShutdownHook(new Thread(()->{this.onClose();}));

    }
    /**
     * Retrieves the port number the server is bound to.
     * @return String representation of the server port
     */
    public String getPort(){
        return Integer.toString(this.port);
    }
    
    /**
     * Gracefully closes the server resources when the application terminates.
     * Closes the Selector and ServerSocketChannel.
     */
    public void onClose(){
         try{
                log.print("Closing Application");
                selector.close();
                sock.close();
            }catch(IOException ex){
                log.eprint(ex);
            }
        
    }
    
    /**
     * Initializes the server by binding to the specified port and registering the ServerSocketChannel
     * with the Selector for accepting incoming connections.
     */
    public void init(){
       InetSocketAddress bindpoint =new InetSocketAddress("0.0.0.0",this.port);
        try {
            sock.bind(bindpoint);
            sock.configureBlocking(false);
            sock.register(selector, SelectionKey.OP_ACCEPT);
            log.print("Successfully binded to %S".formatted(this.port));
           
        }
         catch (IOException e) {
        log.eprint(e);
        }  
    }
    
    /**
     * Registers a GET route with the server.
     * @param path The URL path for this route
     * @param callback The handler function to execute when this route is accessed
     */
    public void get(String path,ReqFunction callback){this.route("GET",path, callback);}
    
    /**
     * Registers a POST route with the server.
     * @param path The URL path for this route
     * @param callback The handler function to execute when this route is accessed
     */
    public void post(String path,ReqFunction callback){this.route("POST",path, callback);}
    
    /**
     * Registers a PATCH route with the server.
     * @param path The URL path for this route
     * @param callback The handler function to execute when this route is accessed
     */
    public void patch(String path,ReqFunction callback){this.route("PATCH",path, callback);}
    
    /**
     * Registers a PUT route with the server.
     * @param path The URL path for this route
     * @param callback The handler function to execute when this route is accessed
     */
    public void put(String path,ReqFunction callback){this.route("PUT",path, callback);}
    
    /**
     * Registers a DELETE route with the server.
     * @param path The URL path for this route
     * @param callback The handler function to execute when this route is accessed
     */
    public void delete(String path,ReqFunction callback){this.route("DELETE",path, callback);}
    
    /**
     * Adds a middleware to the server's middleware chain.
     * Middleware is executed for each request before route handlers.
     * @param mid The middleware to add
     */
    public void add(Middleware mid){
        this.add_Middleware(mid);
    }

    /**
     * Handles incoming HTTP requests by parsing the raw data into a Request object,
     * executing middleware and route handlers, and sending back an HTTP response.
     * If no route is found, responds with a 404 Not Found error.
     * 
     * @param key The SelectionKey representing the client connection
     * @param context The RequestContext containing the incoming request data
     */
    public void RequestHandler(SelectionKey key,RequestContext context){
      
        String res =context.getData();
       
        
        Request req = Request.fromData(res);
        String currentMethod = req.getMethod();

        log.print(currentMethod);
        log.route_print(req.getPath());

        Response response = new Response();
        try {
           this.run(req, response); 
           String d =response.get_header();
           
           SocketChannel client= (SocketChannel) key.channel();

           Charset charset= Charset.forName("UTF-8");
           ByteBuffer bf = charset.encode(d);
           
            try {
               client.write(bf);
               key.interestOps(SelectionKey.OP_READ); 
               
            } catch (IOException ex) { log.eprint(ex); }
           

        } catch (DropRouteException e) {
           
            SocketChannel client = (SocketChannel) key.channel();

           String no404 = response.get_not_found();
           Charset nfcharset = Charset.forName("UTF-8");
           ByteBuffer buffer = nfcharset.encode(no404);
           
           try{
               client.write(buffer);
               key.interestOps(SelectionKey.OP_READ);
           
           } catch (IOException er) { log.eprint(er);}
        }

         
     
    }
    
    /**
     * Enables test mode where the server closes after processing a single request.
     * Useful for testing scenarios.
     */
    public void isTesting(){
        log.libprint("Closing after single request", LogLevel.OK);
        this.should_close_after_one_request = true;
    }
    
    /**
     * Interprets incoming request data from a client socket channel.
     * Reads data into a ByteBuffer, appends it to RequestContext, and delegates
     * to RequestHandler once a complete HTTP request is received (marked by \r\n\r\n).
     * 
     * @param key The SelectionKey for the client connection
     */
    public void RequestInterpreter(SelectionKey key){

    SocketChannel client = (SocketChannel) key.channel();
    RequestContext currentContext = (RequestContext) key.attachment();

    if (currentContext == null){
        currentContext = new RequestContext();
        key.attach(currentContext);
    }
    try{
        int read = client.read(currentContext.byteBuffer);
        if (read==-1){
            client.close();
            return ;
        }
        if (read>0){
            currentContext.append_data(read);
        }
        if (currentContext.isComplete){
            key.interestOps(SelectionKey.OP_WRITE);
           RequestHandler(key,currentContext);
        }

    }catch(IOException ex){
        log.eprint(ex);
    }
    
    }
    
    /**
     * Accepts a new client connection from the ServerSocketChannel.
     * Configures the client SocketChannel for non-blocking I/O and registers it
     * with the Selector for read operations.
     * 
     * @param select The SelectionKey for the accept operation
     */
    public void Acceptence(SelectionKey select){
        
        try {
            SocketChannel clientReq =  sock.accept();
            clientReq.configureBlocking(false);
            clientReq.register(selector, SelectionKey.OP_READ);

            }
             catch (IOException e) {
                 log.eprint(e);
             }
    }
    
    /**
     * Main event loop that continuously monitors registered channels for I/O readiness.
     * Uses the Selector to wait for events (accept, read, write) and delegates
     * processing to appropriate handlers. Runs until should_close_after_one_request is true.
     */
    public void listen(){
      
        boolean loop=true;                                         
        log.libprint("[initializer] Web Server Started",LogLevel.VERBOSE);
        
        while (loop) { 
            try {
               
            selector.select();
            } catch (IOException e) {
            log.eprint(e);
            }
            
          Iterator<SelectionKey> keys=  selector.selectedKeys().iterator();
          
            while(keys.hasNext()){
                SelectionKey key=keys.next();
                keys.remove();
                if(key.isAcceptable()){
                 Acceptence(key);
                }
                else if(key.isReadable()){
                    RequestInterpreter(key);
                }
            }
            
             if (should_close_after_one_request){
                    loop=false;
                }
        }
       
    }

  
    


}

