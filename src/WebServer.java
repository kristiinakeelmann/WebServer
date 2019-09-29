import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServer {

    public static void main(String args[] ) throws IOException {

        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        System.out.println("Listening for connection on port: " + port);

        while (true) {
            try (Socket socket = server.accept()) {
                String httpResponse =
                        "HTTP/1.1 200 OK\n" +
                        "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                        "Server: WebServer\n" +
                        "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                        "Content-Length: 44\n" +
                        "Content-Type: text/html\n" +
                        "Connection: Keep-Alive\n\n" +
                                "<html>" +
                                "<body>" +
                                "<h1>It works!</h1>" +
                                "</body>" +
                                "</html>";
                socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            }
        }
    }

}
