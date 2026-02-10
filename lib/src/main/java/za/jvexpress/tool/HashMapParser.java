package za.jvexpress.tool;

import java.util.LinkedHashMap;

import za.jvexpress.utils.Log;

public class HashMapParser {
    private Log log;
    public HashMapParser() {
        log = new Log();
    }

    
    public static String toJString(LinkedHashMap data){

   
    StringBuilder builder = new StringBuilder();
  
    builder.append("{");
    data.forEach((k,v)->{
        String value="";
        if (v.getClass() == LinkedHashMap.class ){
            System.out.println("Object detected!");
             value=toJString((LinkedHashMap<?,?>) v);
             builder.append(String.format("\"%s\":%s,",k,value));

        }
        else{
            if (v.getClass() == String.class) {
                value = (String) v;
                builder.append(String.format("\"%s\":\"%s\",",k,value));
            }
        }
        });
    builder.deleteCharAt(builder.length()-1);
    builder.append("}");
    System.out.println(builder);
    return builder.toString();

    } 

}
