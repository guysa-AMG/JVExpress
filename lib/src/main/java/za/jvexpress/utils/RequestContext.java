package  za.jvexpress.utils;
import java.nio.ByteBuffer;



public class RequestContext{
    ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

    StringBuilder stringbuilder = new StringBuilder();

    public Boolean isComplete = false;

    public Boolean append_data(int ByteRead){
        byteBuffer.flip();

        byte[] rbyte = new byte[ByteRead];
        
        byteBuffer.get(rbyte);

        stringbuilder.append(new String(rbyte));

        byteBuffer.clear();


        if (stringbuilder.toString().contains("\r\n\r\n")){
            isComplete =true;
        }
        return true;
    }
}