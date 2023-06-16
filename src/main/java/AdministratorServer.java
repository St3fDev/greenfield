import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Scanner;

public class AdministratorServer {
    private static final String HOST = "localhost";
    private static final int PORT = 1337;
    private static final String BROKER_ADDRESS = "tcp://localhost:1883";
    private static final String ID = MqttClient.generateClientId();
    private static final String[] TOPIC = {"greenfield/pollution/district1", "greenfield/pollution/district2", "greenfield/pollution/district3", "greenfield/pollution/district4"};
    private static final int[] QOSs = {2,2,2,2};

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://"+HOST + ":" + PORT + "/");
        server.start();

        System.out.println("Administrator server started!");
        System.out.println("Administrator server started on http://"+HOST + ":" + PORT);

        try(MqttClient client = new MqttClient(AdministratorServer.BROKER_ADDRESS, AdministratorServer.ID)) {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.printf("(%s) connection to the broker %s...\n", AdministratorServer.BROKER_ADDRESS, AdministratorServer.ID);
            client.connect(connOpts);
            System.out.printf("(%s) connected\n", AdministratorServer.ID);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.printf("(%s) connection lost. Caused by: %s\n", AdministratorServer.ID, cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    System.out.println("STAMPO IL VALORE DI PROVA:" + receivedMessage);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // not used
                }
            });

            System.out.printf("(%s) subscribing to the topic...\n", AdministratorServer.ID);
            client.subscribe(AdministratorServer.TOPIC, AdministratorServer.QOSs);
            System.out.printf("(%s) topic registration completed\n", AdministratorServer.ID);

            System.out.println("Press a button to stop...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            System.out.println("Stopping Administrator server");
            server.stop(0);
            System.out.println("Administrator server stopped");
            client.disconnect();
        } catch (MqttException mqttException) {
            System.err.printf("An error has occurred: %s\n", mqttException);
        }
    }

}
