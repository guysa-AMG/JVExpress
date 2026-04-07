
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import za.jvexpress.struct.Request;



class RequestTest{


    @Test
    void testingUrlQueryParameter(){
        String rawResponse = "GET /user?name=john \n" + //
                        "Host: localhost:8089\n" + //
                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:149.0) Gecko/20100101 Firefox/149.0\n" + //
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" + //
                        "Accept-Language: en-US,en;q=0.9\n" + //
                        "Accept-Encoding: gzip, deflate, br, zstd\n" + //
                        "Connection: keep-alive\n" + //
                        "Cookie: _ga=GA1.1.1168414286.1769697006; grafana_session=e894326ea2ddf2a02baecf79255c2908; grafana_session_expiry=1774966068; user=GuySA; mongo-express=s%3A53Vp6Ir8rfiXsHvoZ0CIiNXUJX9u3h5b.TlrUWOOehclbUFiS%2Fjh%2FuE%2BmYAuXIueX3YwhoJAB0Ds\n" + //
                        "Upgrade-Insecure-Requests: 1\n" + //
                        "Sec-Fetch-Dest: document\n" + //
                        "Sec-Fetch-Mode: navigate\n" + //
                        "Sec-Fetch-Site: none\n" + //
                        "Priority: u=0, i";
    Request req =Request.fromData(rawResponse);
    String queries = req.getQueries();
    String expectedString="{user={name=john}}";
    assertEquals(expectedString,queries);
    }

      @Test
    void testingUrlMultiQueryParameter(){
        String rawResponse = "GET /user?name=john&age=50 \n" + //
                        "Host: localhost:8089\n" + //
                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:149.0) Gecko/20100101 Firefox/149.0\n" + //
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" + //
                        "Accept-Language: en-US,en;q=0.9\n" + //
                        "Accept-Encoding: gzip, deflate, br, zstd\n" + //
                        "Connection: keep-alive\n" + //
                        "Cookie: _ga=GA1.1.1168414286.1769697006; grafana_session=e894326ea2ddf2a02baecf79255c2908; grafana_session_expiry=1774966068; user=GuySA; mongo-express=s%3A53Vp6Ir8rfiXsHvoZ0CIiNXUJX9u3h5b.TlrUWOOehclbUFiS%2Fjh%2FuE%2BmYAuXIueX3YwhoJAB0Ds\n" + //
                        "Upgrade-Insecure-Requests: 1\n" + //
                        "Sec-Fetch-Dest: document\n" + //
                        "Sec-Fetch-Mode: navigate\n" + //
                        "Sec-Fetch-Site: none\n" + //
                        "Priority: u=0, i";
    Request req =Request.fromData(rawResponse);
    String queries = req.getQueries();
    String expectedString="{user={name=john, age=50}}";
    assertEquals(expectedString,queries);
    }
      @Test
    void testingUrlQueryParameterWithNullable(){
        String rawResponse = "GET /user?name=john&age \n" + //
                        "Host: localhost:8089\n" + //
                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:149.0) Gecko/20100101 Firefox/149.0\n" + //
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" + //
                        "Accept-Language: en-US,en;q=0.9\n" + //
                        "Accept-Encoding: gzip, deflate, br, zstd\n" + //
                        "Connection: keep-alive\n" + //
                        "Cookie: _ga=GA1.1.1168414286.1769697006; grafana_session=e894326ea2ddf2a02baecf79255c2908; grafana_session_expiry=1774966068; user=GuySA; mongo-express=s%3A53Vp6Ir8rfiXsHvoZ0CIiNXUJX9u3h5b.TlrUWOOehclbUFiS%2Fjh%2FuE%2BmYAuXIueX3YwhoJAB0Ds\n" + //
                        "Upgrade-Insecure-Requests: 1\n" + //
                        "Sec-Fetch-Dest: document\n" + //
                        "Sec-Fetch-Mode: navigate\n" + //
                        "Sec-Fetch-Site: none\n" + //
                        "Priority: u=0, i";
    Request req =Request.fromData(rawResponse);
    String queries = req.getQueries();
    String expectedString="{user={name=john, age=null}}";
    assertEquals(expectedString,queries);
    }


      @Test
    void testingUrlParser(){
        String rawResponse = "GET /my/progress HTTP/2\r\n" + //
                        "Host: test.com\r\n" + //
                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:149.0) Gecko/20100101 Firefox/149.0\r\n" + //
                        "Accept: */*\r\n" + //
                        "Accept-Language: en-US,en;q=0.9\r\n" + //
                        "Accept-Encoding: gzip, deflate, br, zstd\r\n" + //
                        "Referer: https://portal.wethinkco.de/\r\n" + //
                        "authorization: Bearer eyJhbGviOiJSUzI1NuIsImtpZCI6ImExMGU1OGRkNToken\r\n" + //
                        "x-api-key: XbcmSlrOaj8MNRZf0QQb56M9kkRGt8EV7auPr63Z\r\n" + //
                        "Origin: https://portal.wethinkco.de\r\n" + //
                        "Cookie: _forward_auth=XFyCc5LaUGfyGgTwiGpmJcbfEP6SKDyC_GmSUOOfCvw=|1975908592|blob@gmail.com\r\n" + //
                        "Connection: keep-alive\r\n" + //
                        "Sec-Fetch-Dest: empty\r\n" + //
                        "Sec-Fetch-Mode: cors\r\n" + //
                        "Sec-Fetch-Site: same-site";
    Request req =Request.fromData(rawResponse);
    assertEquals("Bearer eyJhbGviOiJSUzI1NuIsImtpZCI6ImExMGU1OGRkNToken",req.getAuthorization());
    assertEquals("Mozilla/5.0 (X11; Linux x86_64; rv:149.0) Gecko/20100101 Firefox/149.0",req.getUserAgent());
    assertEquals("keep-alive", req.getConnection());
    assertEquals("_forward_auth=XFyCc5LaUGfyGgTwiGpmJcbfEP6SKDyC_GmSUOOfCvw=|1975908592|blob@gmail.com", req.getCookie());
 
    }
}