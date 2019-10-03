import java.io.IOException;


public class WebApplication {

    public static void main(String[] args) throws IOException {

        WebServer webServer = new WebServer(8080);
        webServer.addHtml("yolo", "reached yolo");
        webServer.addHtml("sup", "reached sup");
        webServer.addHtml("mina", "mina olen pdf");
        webServer.addFile("pdf", "resources/sample.pdf");
        webServer.start();
    }
}
