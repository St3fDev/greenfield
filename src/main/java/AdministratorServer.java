import MQTT.ServerMqttSubscriber;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;

public class AdministratorServer {
    private static final String HOST = "localhost";
    private static final int PORT = 1337;
    private static final String BROKER_ADDRESS = "tcp://localhost:1883";
    private static final String ID = MqttClient.generateClientId();
    private static final String[] TOPICS = {"greenfield/pollution/district1", "greenfield/pollution/district2", "greenfield/pollution/district3", "greenfield/pollution/district4"};
    private static final int[] QOSs = {2,2,2,2};

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://"+HOST + ":" + PORT + "/");
        server.start();

        System.out.println("Administrator server started!");
        System.out.println("Administrator server started on http://"+HOST + ":" + PORT);

        ServerMqttSubscriber serverSubscriber = new ServerMqttSubscriber(server);
        serverSubscriber.startMQTTSubscriber();
    }


}
