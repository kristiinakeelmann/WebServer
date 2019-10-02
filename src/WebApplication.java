import java.io.IOException;
import java.util.HashMap;


public class WebApplication {

    public static void main(String[] args) throws IOException {

        HashMap<String, String> routing = new HashMap<>();
        routing.put("yolo", "reached yolo");
        routing.put("sup", "reached sup");
        routing.put("pdf", "resources/sample.pdf");


        WebServer webServer = new WebServer(8080);
        webServer.addPath(routing);
        webServer.start();
    }
}
