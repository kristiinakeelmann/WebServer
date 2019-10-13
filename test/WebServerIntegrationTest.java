import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class WebServerIntegrationTest {

    @Test
    public void getRequest() throws IOException {

        Thread thread = new Thread(() -> {
            WebServer webServer = new WebServer(8040);
            webServer.addHtml("sup", "sup");
            try {
                webServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();


        Socket socket = new Socket("localhost", 8040);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("GET /sup HTTP/1.1");
        out.println("Host: localhost/8040");
        out.println("Connection: Close");

        boolean loop = true;
        StringBuilder sb = new StringBuilder(8096);
        while (loop) {
            if (in.ready()) {
                int i = 0;
                while (i != -1) {
                    i = in.read();
                    sb.append((char) i);
                }
                loop = false;
            }
        }


        String result = sb.toString();
        String resultAsString = (result);
        assertEquals("HTTP/1.1 200 OK\n" +
                "Server: WebServer\n" +
                "Content-Length: 38\n" +
                "Content-Type: text/html\n" +
                "\n" +
                "<html><body><h1>sup</h1></body></html>\uFFFF", resultAsString);
    }
}