package za.jvexpress;
import java.net.*;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import za.jvexpress.utils.RequestContext;
import za.jvexpress.utils.Log;


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
    }

    public  void init(){
       InetSocketAddress bindpoint =new InetSocketAddress("0.0.0.0",8080);
        try {
            sock.bind(bindpoint);
            sock.configureBlocking(false);
            sock.register(selector, SelectionKey.OP_ACCEPT);
            log.print("Successfully binded to {}".formatted(port));
        }
         catch (IOException e) {
        log.eprint(e);
        }  
    }
    public void RequestInterpreter(SelectionKey key){

    SocketChannel client = (SocketChannel) key.channel();
    
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