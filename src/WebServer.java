import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

interface Action {
}

class HtmlAction implements Action {
    String html;
}

class FileAction implements Action {
    String filename;
}

class RedirectAction implements Action {
    String target;
}

class NotFoundAction implements Action {
}


public class WebServer {

    int port;
    HashMap<String, Action> routing = new HashMap<>();

    public WebServer(int port) {
        this.port = port;
    }

    public void addHtml(String path, String html) {
        HtmlAction htmlAction = new HtmlAction();
        htmlAction.html = html;
        routing.put(path, htmlAction);
    }

    public void addFile(String path, String filename) {
        FileAction fileAction = new FileAction();
        fileAction.filename = filename;
        routing.put(path, fileAction);
    }

    public void addRedirect(String path, String target) {
        RedirectAction redirectAction = new RedirectAction();
        redirectAction.target = target;
        routing.put(path, redirectAction);
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

        Action action = null;
        String path = "";

        Set set = routing.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            path = entry.getKey().toString();

            if (httpRequest.contains(path)) {
                action = (Action) entry.getValue();
                return actionToResponse(action);
            }
        }

        NotFoundAction notFoundAction = new NotFoundAction();
        return actionToResponse(notFoundAction);

    }

    private static byte[] actionToResponse(Action action) throws IOException {

        String statusCode;
        String redirectLocation = "";
        String contentType;
        String httpResponseHeader;
        byte[] httpResponseBody = null;

        if (action instanceof FileAction) {
            httpResponseBody = composeHttpResponseBodyPdf();
        }
        if (action instanceof HtmlAction) {
            HtmlAction htmlAction = (HtmlAction) action;
            httpResponseBody = composeHttpResponseBody(htmlAction.html);
        }
        if (action instanceof RedirectAction) {
            RedirectAction redirectAction = (RedirectAction) action;
            redirectLocation = redirectAction.target;
            httpResponseBody = composeHttpResponseBody("Redirecting");
        }
        if (action instanceof NotFoundAction) {
            httpResponseBody = composeHttpResponseBody("It must have disappeared");
        }

        statusCode = responseStatusCode(action);
        int contentLength = httpResponseBody.length;
        contentType = responseContentType(action);
        httpResponseHeader = composeHttpResponseHeader(statusCode, redirectLocation, contentLength, contentType);
        byte header[] = httpResponseHeader.getBytes();
        byte body[] = httpResponseBody;
        return getHttpResponseBytes(header, body);
    }

    private static byte[] getHttpResponseBytes(byte[] header, byte[] body) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(header);
        outputStream.write(body);
        byte httpResponse[] = outputStream.toByteArray();
        return httpResponse;
    }

    private static String responseContentType(Action action) {

        if (action instanceof FileAction) {
            String contentType = "application/pdf";
            return contentType;
        }
        String contentType = "text/html";
        return contentType;
    }

    private static String responseStatusCode(Action action) {

        if (action instanceof RedirectAction) {
            String statusCode = "302 Found";
            return statusCode;
        }

        if (action instanceof NotFoundAction) {
            String statusCode = "404 Not Found";
            return statusCode;
        }

        String statusCode = "200 OK";
        return statusCode;
    }


    public static String composeHttpResponseHeader(String statusCode, String redirectLocation, int contentLength, String contentType) {

        String httpResponseHeader =
                "HTTP/1.1 " + statusCode + "\n" +
                        "Location: " + redirectLocation + "\n" +
                        "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                        "Server: WebServer\n" +
                        "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                        "Content-Length: " + contentLength + "\n" +
                        "Content-Type: " + contentType + "\n" +
                        "Content-Disposition: inline" + "\n" +
                        "Connection: Keep-Alive\n\n";
        return httpResponseHeader;
    }


    public static byte[] composeHttpResponseBody(String message) {

        String httpResponseBody =
                "<html>" +
                        "<body>" +
                        "<h1>" + message + "</h1>" +
                        "</body>" +
                        "</html>";
        return httpResponseBody.getBytes();
    }

    public static byte[] composeHttpResponseBodyPdf() throws IOException {

        byte[] data = WebServer.class.getClassLoader().getResourceAsStream("resources/sample.pdf").readAllBytes();
        return data;
    }
}