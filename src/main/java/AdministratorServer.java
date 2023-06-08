import beans.CleaningRobot;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class AdministratorServer {
    private static final String HOST = "localhost";
    private static final int PORT = 1337;
    private static final String BROKER_ADDRESS = "tcp://localhost:1883";
    private static final String ID = MqttClient.generateClientId();
    private static final String topic = "greenfield/pollution/district/*";
    private static final int QOS = 2;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://"+HOST + ":" + PORT + "/");
        server.start();

        System.out.println("Administrator server started!");
        System.out.println("Administrator server started on http://"+HOST + ":" + PORT);
        new Scanner(System.in).nextLine();
        System.out.println("Stopping Administrator server");
        server.stop(0);
        System.out.println("Administrator server stopped");

        /*try(MqttClient client = new MqttClient(AdministratorServer.BROKER_ADDRESS, AdministratorServer.ID)) {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.printf("(%s) connection to the broker %s...\n", AdministratorServer.BROKER_ADDRESS, AdministratorServer.ID);
            client.connect(connOpts);
            System.out.printf("(%s) connesso\n", AdministratorServer.ID);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.printf("(%s) connection lost. Caused by: %s\n", AdministratorServer.ID, cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            System.out.printf("(%s) subscribing to the topic...\n", AdministratorServer.ID);
            client.subscribe(topics, AdministratorServer.QOSs);
            System.out.printf("(%s) topic registration completed\n", AdministratorServer.ID);

            System.out.println("Press a button to stop...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            client.disconnect();
        } catch (MqttException mqttException) {
            System.err.printf("An error has occurred: %s\n", mqttException);
        }*/
    }

}
