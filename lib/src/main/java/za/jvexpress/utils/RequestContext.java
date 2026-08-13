package  za.jvexpress.utils;
import java.nio.ByteBuffer;

/**
 * Holds the state of an incoming HTTP request as it is read from the network.
 * Accumulates data in a ByteBuffer and detects when a complete HTTP request has been received.
 */
public class RequestContext{
  public  ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

    StringBuilder stringbuilder = new StringBuilder();
    Log log ;
    public Boolean isComplete = false;

    /**
     * Constructs a new RequestContext.
     */
    public RequestContext() {
    log = new Log();
    }
    
    /**
     * Gets the accumulated request data as a string.
     * @return The raw HTTP request data
     */
    public String getData(){
        return stringbuilder.toString();
    }
    
    /**
     * Appends newly read bytes to the request data.
     * Detects completion when the request includes the HTTP header terminator (\r\n\r\n).
     * 
     * @param ByteRead Number of bytes read from the socket
     * @return true if data was successfully appended
     */
    public Boolean append_data(int ByteRead){
        byteBuffer.flip();

        byte[] rbyte = new byte[ByteRead];
        
        byteBuffer.get(rbyte);
        String nstring =new String(rbyte);

       

        stringbuilder.append(nstring);

        byteBuffer.clear();


        if (stringbuilder.toString().contains("\r\n\r\n")){
             
            this.isComplete =true;
        }
        return true;
    }
}