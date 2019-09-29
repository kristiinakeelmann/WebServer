import java.io.IOException;


public class WebApplication {

    public static void main(String[] args) throws IOException {

        WebServer webServer = new WebServer(8080);
        webServer.addPath("yolo", "reached yolo");
        webServer.start();
    }
}
