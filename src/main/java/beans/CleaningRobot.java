package beans;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.*;

public class CleaningRobot {
    private static final String BROKER_ADDRESS = "tcp://localhost:1883";
    private static final String ID = MqttClient.generateClientId();
    private static final int QOS = 2;

    private List<CleaningRobotData> robotsInGreenfield = new ArrayList<>();

    public static void main(String[] args) {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337";
        ClientResponse clientResponse = null;


        String postPath = "/robots/addRobot";
        //TODO aggiungere parametri corretti per address e port
        CleaningRobotData cleaningRobot = new CleaningRobotData(ID, "localhost", "1234");
        clientResponse = postRequest(client, serverAddress + postPath, cleaningRobot);
        System.out.println(clientResponse.toString());

        removeRobot(client);
        //clientResponse = deleteRequest(client, serverAddress + removePath, ID);
        //System.out.println(clientResponse.toString());
    }

    private static ClientResponse postRequest(Client client, String url, CleaningRobotData cleaningRobot){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(cleaningRobot);
        try {
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

    //TODO gestire la delete dopo aver configurato tutto il model e il client/server
    private static void removeRobot(Client client) {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            String input = scanner.nextLine();
            while (!input.equalsIgnoreCase("quit")) {
                input = scanner.nextLine();
            }
            String removePath = "/removeRobot/" + ID;
            deleteRequest(client,removePath);
        });
        inputThread.start();
    }

}
