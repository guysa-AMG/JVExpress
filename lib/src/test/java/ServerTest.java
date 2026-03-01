
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import za.jvexpress.Server;
import za.jvexpress.tool.HashMapParser;


public class ServerTest {

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
    @Test
    void testGetRequest() throws Exception {
    Server server = new Server(0); 
    server.get("/test", (req, res) -> res.send("OK"));
    server.listen();

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" +server.getPort() + "/test"))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(200, response.statusCode());
    assertEquals("OK", response.body());
} 

}
