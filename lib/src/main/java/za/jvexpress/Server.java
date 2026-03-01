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


public  class Server extends RequestFlow{
    private ServerSocketChannel sock; 
    private Selector selector;
    private Log log;
   
    
   
    private int port;
    

    public Server(int qport) {
        
        if(qport==0){
            port =8080;
        }
        else{
            port =qport;
        }
        port = qport;
    try {
        sock =  ServerSocketChannel.open();
         selector=  Selector.open();
         
    } 
    catch (IOException e) {
        log.eprint(e);
    }
      log = new Log();
      Runtime.getRuntime().addShutdownHook(new Thread(()->{this.onClose();}));


    }
    
    public String getPort(){
        return Integer.toString(this.port);
    }
    public void onClose(){
         try{
                log.print("Closing Application");
                selector.close();
                sock.close();
            }catch(IOException ex){
                log.eprint(ex);
            }
        
    }
    
    public  void init(){
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
    public void get(String path,ReqFunction callback){this.route("GET",path, callback);}
    
    public void post(String path,ReqFunction callback){this.route("POST",path, callback);}
    
    public void patch(String path,ReqFunction callback){this.route("PATCH",path, callback);}
    
    public void put(String path,ReqFunction callback){this.route("PUT",path, callback);}
    
    public void delete(String path,ReqFunction callback){this.route("DELETE",path, callback);}
    
    public void add(Middleware mid){
        this.add_Middleware(mid);
    }

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
            

        }
       
    }

  
    


}

