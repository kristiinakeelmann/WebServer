import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class WebServer {

    int port;
    HashMap<String, String> routing;

    public WebServer(int port) {
        this.port = port;
    }

    public void addPath(HashMap<String, String> routing) {
        this.routing = routing;
    }

    public void start() throws IOException {

        ServerSocket server = new ServerSocket(port);
        System.out.println("Listening for connection on port: " + port);

        while (true) {
            try (Socket socket = server.accept()) {

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String httpRequest = reader.readLine();
                byte[] httpResponse = handleRequest(httpRequest);
                OutputStream output = socket.getOutputStream();
                output.write(httpResponse);
            }
        }
    }

    private byte[] handleRequest(String httpRequest) throws IOException {

        String message = "";
        String path = "";

        Set set = routing.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            path = entry.getKey().toString();

            if (httpRequest.contains(path)) {
                message = entry.getValue().toString();
                return messageToResponse(message);
            }
        }

        return messageToResponse("Unkown");
    }

    private static byte[] messageToResponse(String message) throws IOException {

        String contentType;
        String httpResponseHeader;

        if (message.contains("pdf")) {
            int contentLength = composeHttpResponseBodyPdf().length;
            contentType = responseContentType(message);
            httpResponseHeader = composeHttpResponseHeader(contentLength, contentType);
            byte httpResponseBody[] = composeHttpResponseBodyPdf();
            byte header[] = httpResponseHeader.getBytes();
            byte body[] = httpResponseBody;

            return getHttpResponseBytes(header, body);
        }

        String httpResponseBody = composeHttpResponseBody(message);
        int contentLength = httpResponseBody.length();
        contentType = responseContentType(message);
        httpResponseHeader = composeHttpResponseHeader(contentLength, contentType);
        byte header[] = httpResponseHeader.getBytes();
        byte body[] = httpResponseBody.getBytes();

        return getHttpResponseBytes(header, body);
    }

    private static byte[] getHttpResponseBytes(byte[] header, byte[] body) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(header);
        outputStream.write(body);
        byte httpResponse[] = outputStream.toByteArray();
        return httpResponse;
    }

    private static String responseContentType(String message) {

        if (message.contains("pdf")) {
            String contentType = "application/pdf";
            return contentType;
        }
        String contentType = "text/html";
        return contentType;
    }

    public static String composeHttpResponseHeader(int contentLength, String contentType) {

        String httpResponseHeader =
                "HTTP/1.1 200 OK\n" +
                        "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                        "Server: WebServer\n" +
                        "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                        "Content-Length: " + contentLength + "\n" +
                        "Content-Type: " + contentType + "\n" +
                        "Content-Disposition: inline" + "\n" +
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

    public static byte[] composeHttpResponseBodyPdf() throws IOException {

        byte[] data = WebServer.class.getClassLoader().getResourceAsStream("resources/sample.pdf").readAllBytes();
        return data;
    }
}