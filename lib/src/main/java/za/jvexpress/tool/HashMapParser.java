package za.jvexpress.tool;

import java.util.Map;

import za.jvexpress.utils.Log;

public class HashMapParser {
    private Log log;
    public HashMapParser() {
        log = new Log();
    }

    
    public static String toJString(Map data){

   
    StringBuilder builder = new StringBuilder();
  
    builder.append("{");
    data.forEach((k,v)->builder.append(String.format("\"%s\":\"%s\",",k,v)));
    builder.deleteCharAt(builder.length()-1);
    builder.append("}");
    System.out.println(builder.toString());
    return builder.toString();

    } 

}
