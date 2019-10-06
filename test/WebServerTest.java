import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;

public class WebServerTest {

    @Test
    public void actionToResponse_htmlAction() throws IOException {

        HtmlAction action = new HtmlAction();
        action.html = "reached end of world";

        byte[] result = WebServer.actionToResponse(action);

        String resultAsString = new String(result);
        assertEquals("HTTP/1.1 200 OK\n" +
                "Server: WebServer\n" +
                "Content-Length: 55\n" +
                "Content-Type: text/html\n" +
                "\n" +
                "<html><body><h1>reached end of world</h1></body></html>", resultAsString);
    }

    @Test
    public void actionToResponse_fileAction() throws IOException {

        FileAction action = new FileAction();
        action.filename = "resources/blank.pdf";

        byte[] result = WebServer.actionToResponse(action);

        String resultAsString = new String(result);
        assertTrue(resultAsString.startsWith("HTTP/1.1 200 OK\n" +
                "Server: WebServer\n" +
                "Content-Length: 4911\n" +
                "Content-Type: application/pdf\n" +
                "\n" +
                "%PDF-1.6"
        ));
    }
}