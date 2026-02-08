
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import za.jvexpress.tool.HashMapParser;

public class ServerTest {

    @Test
    public void test_json_parser(){
        Map data =new TreeMap<String,String>();
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
}
