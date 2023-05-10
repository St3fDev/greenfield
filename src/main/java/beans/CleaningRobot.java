package beans;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.eclipse.paho.client.mqttv3.MqttClient;

public class CleaningRobot {
    private static final String BROKER_ADDRESS = "tcp://localhost:1883";
    private static final String ID = MqttClient.generateClientId();
    private static final int QOS = 2;

    //TODO capire come definire la porta
    private String topic;
    private Cell position;

    private void setPosition(Cell position) {
        this.position = position;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Cell getPosition() {
        return position;
    }
    public String getId() {
        return ID;
    }


    //TODO scrivere metodo che ritorna la porta

    public static void main(String[] args) {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337";
        ClientResponse clientResponse = null;

        String postPath = "/robots/" + ID;
        //TODO aggiungere parametri corretti per address e port
        CleaningRobotInfo cleaningRobot = new CleaningRobotInfo(ID, "localhost", "1234");
        clientResponse = postRequest(client, serverAddress + postPath, cleaningRobot);
        System.out.println(clientResponse.toString());
    }

    public static ClientResponse postRequest(Client client, String url, CleaningRobotInfo cleaningRobot){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(cleaningRobot);
        System.out.println(input);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server non disponibile");
            return null;
        }
    }

}

// TODO risolvere il motivo per cui non va la post
// TODO capire come gestire i robots: mappa e passaggio tramite REST