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
                String httpResponseHeader = composeHttpResponseHeader();
                String httpResponseBody = composeHttpResponseBody();
                String httpResponse = composeHttpResponse(httpResponseHeader, httpResponseBody);
                socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            }
        }
    }

    public static String composeHttpResponseHeader(){
        var contentLength = composeHttpResponseBody().length();
        String httpResponseHeader =
                        "HTTP/1.1 200 OK\n" +
                        "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                        "Server: WebServer\n" +
                        "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                        "Content-Length: " + contentLength + "\n" +
                        "Content-Type: text/html\n" +
                        "Connection: Keep-Alive\n\n";
        return httpResponseHeader;
    }

    public static String composeHttpResponseBody(){
        var message = "It works!";
        String httpResponseBody =
                        "<html>" +
                        "<body>" +
                        "<h1>" + message + "</h1>" +
                        "</body>" +
                        "</html>";
        return httpResponseBody;
    }

    public static String composeHttpResponse(String httpResponseHeader, String httpResponseBody) {
        String httpResponse = httpResponseHeader + httpResponseBody;
        return httpResponse;
    }
}
