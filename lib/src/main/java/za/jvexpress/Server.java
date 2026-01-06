package za.jvexpress;

import java.net.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import za.jvexpress.utils.RequestContext;
import za.jvexpress.utils.Log;
import za.jvexpress.utils.Log.LogLevel;
import za.jvexpress.struct.Request;
import java.lang.Runtime;

public  class Server{
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
       InetSocketAddress bindpoint =new InetSocketAddress("0.0.0.0",8080);
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
    public void RequestHandler(SelectionKey key,RequestContext context){
        String res =context.getData();
       
        
        Request req = Request.fromData(res);
         log.print(req.getMethod());
        log.route_print(req.getPath());
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

