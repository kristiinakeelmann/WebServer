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

    @Test
    public void actionToResponse_redirectAction() throws IOException {

        RedirectAction action = new RedirectAction();
        action.target = "https://www.err.ee";

        byte[] result = WebServer.actionToResponse(action);

        String resultAsString = new String(result);
        assertEquals("HTTP/1.1 302 Found\n" +
                "Location: https://www.err.ee\n" +
                "Server: WebServer\n" +
                "Content-Length: 46\n" +
                "Content-Type: text/html\n" +
                "\n" +
                "<html><body><h1>Redirecting</h1></body></html>", resultAsString);
    }

    @Test
    public void actionToResponse_notFoundAction() throws IOException {

        NotFoundAction action = new NotFoundAction();

        byte[] result = WebServer.actionToResponse(action);

        String resultAsString = new String(result);
        assertEquals("HTTP/1.1 404 Not Found\n" +
                "Server: WebServer\n" +
                "Content-Length: 59\n" +
                "Content-Type: text/html\n" +
                "\n" +
                "<html><body><h1>It must have disappeared</h1></body></html>", resultAsString);
    }
}