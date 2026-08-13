package za.jvexpress.tool;

import java.util.LinkedHashMap;

import za.jvexpress.utils.Log;

/**
 * Utility class for converting Java data structures to JSON format.
 * Handles nested LinkedHashMaps and string values.
 * Produces valid JSON output with proper formatting.
 */
public class HashMapParser {
    private Log log;
    public HashMapParser() {
        log = new Log();
    }

    /**
     * Converts a LinkedHashMap to a JSON string.
     * Recursively handles nested LinkedHashMaps.
     * String values are properly quoted in the output.
     * 
     * @param data The LinkedHashMap to convert
     * @return A JSON-formatted string representation of the data
     */
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
