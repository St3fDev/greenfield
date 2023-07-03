package server;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import server.MQTT.ServerMqttSubscriber;

import java.io.IOException;

public class AdministratorServer {
    private static final String HOST = "localhost";
    private static final int PORT = 1337;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://"+HOST + ":" + PORT + "/");
        server.start();

        System.out.println("Administrator server started!");
        System.out.println("Administrator server started on http://"+HOST + ":" + PORT);

        ServerMqttSubscriber serverSubscriber = new ServerMqttSubscriber(server);
        serverSubscriber.startMQTTSubscriber();
    }


}
