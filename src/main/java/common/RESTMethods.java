package common;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import robot.beans.CleaningRobotDetails;

public class RESTMethods {

    private static final String SERVER_ADDRESS = "http://localhost:1337";

    //-------------------------------------- ROBOT METHOD -----------------------------------------------
    public static ClientResponse postRequest(Client client) {
        String postPath = SERVER_ADDRESS + "/robots/addRobot";
        WebResource webResource = client.resource(postPath);
        String input = new Gson().toJson(CleaningRobotDetails.getInstance().getRobotInfo());
        //System.out.println(input);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server unavailable");
            return null;
        }
    }

    public static void deleteRequest(String robotId) {
        Client client = Client.create();
        String url = SERVER_ADDRESS + "/robots/removeRobot/" + robotId;
        WebResource webResource = client.resource(url);
        try {
            webResource.type("application/json").delete(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server unavailable");
        }
    }

    //------------------------------------- CLIENT METHOD ----------------------------------------------
    public static ClientResponse showCurrentListCleaningRobot(Client client) {
        WebResource webResource = client.resource(SERVER_ADDRESS + "/adminClient/getRobots");
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server not available");
            return null;
        }
    }

    public static ClientResponse getLastNAveragePollutionLevelOfRobot(Client client, String id, int value) {
        WebResource webResource = client.resource(SERVER_ADDRESS + "/adminClient/last_n_avg_pollution/"+ id + "/" + value);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server not available");
            return null;
        }
    }

    public static ClientResponse getAverageAirPollutionLevelsFromTimestamp(Client client, Long t1, Long t2) {
        WebResource webResource = client.resource(SERVER_ADDRESS + "/adminClient/average_pollution_level/"+ t1 + "/" + t2);
        try {
            return webResource.type("application&/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server not available");
            return null;
        }
    }
}
