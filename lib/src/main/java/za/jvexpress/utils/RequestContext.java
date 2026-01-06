package  za.jvexpress.utils;
import java.nio.ByteBuffer;



public class RequestContext{
  public  ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

    StringBuilder stringbuilder = new StringBuilder();
    Log log ;
    public Boolean isComplete = false;

    public RequestContext() {
    log = new Log();
    }
    
    public String getData(){
        return stringbuilder.toString();
    }
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