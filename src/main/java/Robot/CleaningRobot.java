package Robot;

import com.google.gson.Gson;
import com.sun.jersey.api.client.*;
import org.eclipse.paho.client.mqttv3.*;

import java.util.*;

public class CleaningRobot {
    private static final String BROKER_ADDRESS = "tcp://localhost:1883";
    private static final String ID = MqttClient.generateClientId();
    private static final int QOS = 2;
    private static final int SLEEP_TIME = 15 * 1_000;
    private static String topic = "greenfield/pollution/district1";
    private static String server_address;
    private static String port;
    private static List<CleaningRobotData> robotsInGreenfield = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337";
        ClientResponse clientResponse = null;


        String postPath = "/robots/addRobot";
        //TODO aggiungere parametri corretti per address e port
        setRobotsDetails();
        CleaningRobotData cleaningRobot = new CleaningRobotData(ID, server_address, port);
        clientResponse = postRequest(client, serverAddress + postPath, cleaningRobot);
        System.out.println(clientResponse.toString());
        removeRobot(client, serverAddress);
        while (true) {
            // Si genera la temperatura (un numero da 18 a 22).
            Random rand = new Random();
            // Obtain a number between [0 - 49].
            String payload = String.valueOf(rand.nextInt(50));
            // Si iniva il messaggio.
            try (MqttClient clientMqtt = new MqttClient(CleaningRobot.BROKER_ADDRESS, CleaningRobot.ID)) {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);

                // Connessione del client.
                System.out.printf("(%s) connessione al broker %s...\n", CleaningRobot.ID, CleaningRobot.BROKER_ADDRESS);
                clientMqtt.connect(connOpts);
                System.out.printf("(%s) connesso\n", CleaningRobot.ID);

                // Si definisce il callback.
                clientMqtt.setCallback(new MqttCallback() {
                    public void messageArrived(String topic, MqttMessage message) {
                        // Non utilizzato dai publisher.
                    }

                    public void connectionLost(Throwable cause) {
                        System.out.printf("(%s) connessione persa. Causa: %s\n", CleaningRobot.ID, cause.getMessage());
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                        if (token.isComplete()) {
                            System.out.printf("(%s) messaggio consegnato con successo\n", CleaningRobot.ID);
                        }
                    }
                });

                // Si genera il messaggio.
                MqttMessage message = new MqttMessage(payload.getBytes());

                // Si definisce il QoS.
                message.setQos(CleaningRobot.QOS);
                System.out.printf("(%s) pubblicazione del nuovo messaggio: %s...\n", CleaningRobot.ID, payload);
                // Si pubblica il messaggio.
                clientMqtt.publish(CleaningRobot.topic, message);
                System.out.printf("(%s) messaggio pubblicato\n", CleaningRobot.ID);

                // Si effettua la disconnessione.
                if (clientMqtt.isConnected()) clientMqtt.disconnect();
                System.out.printf("Sensore %s disconnesso\n", CleaningRobot.ID);
            } catch (MqttException mqttException) {
                System.err.printf("Si è verificato un errore: %s\n", mqttException);
            }

            // Si aspettano cinque secondi e si invia nuovamente un messaggio.
            Thread.sleep(CleaningRobot.SLEEP_TIME);
        }
        //clientResponse = deleteRequest(client, serverAddress + removePath, ID);
        //System.out.println(clientResponse.toString());
    }

    private static void setRobotsDetails() {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the server address: ");
        System.out.print(">>");
        server_address = in.next();
        System.out.println("Enter the server port: ");
        System.out.print(">>");
        port = in.next();
    }

    private static ClientResponse postRequest(Client client, String url, CleaningRobotData cleaningRobot){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(cleaningRobot);
        try {
            //return response.getEntity(new GenericType<List<CleaningRobotData>>() {});
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server unavailable");
            return null;
        }
    }


    private static ClientResponse deleteRequest(Client client, String url) {
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").delete(ClientResponse.class);
        } catch (ClientHandlerException e){
            System.out.println("Server unavailable");
            return null;
        }
    }

    private static void removeRobot(Client client, String serverAddress) {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            String input = scanner.nextLine();
            while (!input.equalsIgnoreCase("quit")) {
                input = scanner.nextLine();
            }
            String removePath = serverAddress + "/robots/removeRobot/" + ID;
            deleteRequest(client,removePath);
            System.exit(0);
        });
        inputThread.start();
    }

}
