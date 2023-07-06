import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import server.MQTT.ServerMqttSubscriber;

import java.io.IOException;
import java.util.logging.Logger;

public class AdministratorServer {
    private static final String HOST = "localhost";
    private static final int PORT = 1337;
    private static final Logger LOG = Logger.getLogger(AdministratorServer.class.getName());

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://"+HOST + ":" + PORT + "/");
        server.start();

        LOG.info("Administrator server started!");
        LOG.info("Administrator server started on http://"+HOST + ":" + PORT);

        ServerMqttSubscriber serverSubscriber = new ServerMqttSubscriber(server);
        serverSubscriber.startMQTTSubscriber();
    }


}
