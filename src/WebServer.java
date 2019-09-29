import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServer {

    int port;

    public WebServer(int port) {
        this.port = port;
    }

    String path;
    String message;

    public void addPath(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public void start() throws IOException {

        ServerSocket server = new ServerSocket(port);
        System.out.println("Listening for connection on port: " + port);

        while (true) {
            try (Socket socket = server.accept()) {

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String httpRequest = reader.readLine();
                String httpResponse = handleRequest(httpRequest);
                socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            }
        }
    }

    private String handleRequest(String httpRequest) {
        var message = "";
        if (httpRequest.contains(path)) {
            message = "You reached yolo path";
        } else {
            message = "Unknown";
        }

        return messageToResponse(message);
    }

    private static String messageToResponse(String message) {

        String httpResponseBody = composeHttpResponseBody(message);
        int contentLength = httpResponseBody.length();
        String httpResponseHeader = composeHttpResponseHeader(contentLength);
        return composeHttpResponse(httpResponseHeader, httpResponseBody);
    }

    public static String composeHttpResponseHeader(int contentLength) {
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

    public static String composeHttpResponseBody(String message) {
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
