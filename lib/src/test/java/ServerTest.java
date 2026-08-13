
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import za.jvexpress.tool.HashMapParser;

/**
 * Unit tests for JSON serialization from LinkedHashMap objects.
 * Tests single-level and nested object conversion to JSON format.
 * Validates proper quote escaping and formatting in generated JSON.
 */
public class ServerTest {

    /**
     * Tests converting a flat LinkedHashMap to JSON format.
     * Verifies that string values are properly quoted and key-value pairs are correctly formatted.
     */
    @Test
    public void test_json_parser(){
        LinkedHashMap data =new LinkedHashMap<String,String>();
        data.put("name", "mark");
        data.put("age", "34");
        data.put("gender", "male");
        data.put("profession", "Developer");
        data.put("overall", "64");

        String expected="{\"name\":\"mark\",\"age\":\"34\",\"gender\":\"male\",\"profession\":\"Developer\",\"overall\":\"64\"}";

        String ret = HashMapParser.toJString(data);
        System.err.println(ret);
        assertEquals(expected, ret);
    }
    
    /**
     * Tests converting a nested LinkedHashMap (parent containing child object) to JSON format.
     * Verifies that nested objects are properly recursively converted and formatted.
     */
    @Test
    public void nest_parser(){
        LinkedHashMap child =new LinkedHashMap<String,String>();
        LinkedHashMap parent =new LinkedHashMap<String,LinkedHashMap>();
        child.put("name", "mark");
        child.put("age", "34");
        child.put("gender", "male");
        child.put("profession", "Developer");
        child.put("overall", "64");

        parent.put("name", "mark");
        parent.put("age", "34");
        parent.put("gender", "male");
        parent.put("profession", "Developer");
        parent.put("overall", "64");
        parent.put("child",child);
        String val = HashMapParser.toJString(parent);
        String expected = "{\"name\":\"mark\",\"age\":\"34\",\"gender\":\"male\",\"profession\":\"Developer\",\"overall\":\"64\",\"child\":{\"name\":\"mark\",\"age\":\"34\",\"gender\":\"male\",\"profession\":\"Developer\",\"overall\":\"64\"}}";
        assertEquals(expected,val);




    }
    
    /**
     * Placeholder test for query parameter functionality.
     * Currently empty - intended for future query parameter testing.
     */
    @Test
    void testQueryParameters(){
        
    }

    // @Test
    // void testGetRequest() throws Exception {
        
    // Server server = new Server(0); 
    // server.get("/test", (req, res) -> res.send("OK"));
        
   


}
