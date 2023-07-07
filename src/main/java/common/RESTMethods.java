package common;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import robot.beans.CleaningRobotModel;

import java.util.logging.Logger;

public class RESTMethods {

    private static final String SERVER_ADDRESS = "http://localhost:1337";
    private static final Logger LOG = Logger.getLogger(RESTMethods.class.getName());

    //-------------------------------------- ROBOT METHOD -----------------------------------------------
    public static ClientResponse postRequest(Client client) {
        String postPath = SERVER_ADDRESS + "/robots/add_robot";
        WebResource webResource = client.resource(postPath);
        String input = new Gson().toJson(CleaningRobotModel.getInstance().getRobotInfo());
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            LOG.warning("Server not reachable");
            return null;
        }
    }

    public static ClientResponse deleteRequest(String robotId) {
        Client client = Client.create();
        String url = SERVER_ADDRESS + "/robots/remove_robot/" + robotId;
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").delete(ClientResponse.class);
        } catch (ClientHandlerException e) {
            LOG.warning("Server not reachable");
            return null;
        }
    }

    //------------------------------------- CLIENT METHOD ----------------------------------------------
    public static ClientResponse showCurrentListCleaningRobot(Client client) {
        WebResource webResource = client.resource(SERVER_ADDRESS + "/admin_client/get_robots");
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            LOG.warning("Server not reachable");
            return null;
        }
    }

    public static ClientResponse getLastNAveragePollutionLevelOfRobot(Client client, String id, int value) {
        WebResource webResource = client.resource(SERVER_ADDRESS + "/admin_client/last_n_avg_pollution/"+ id + "/" + value);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            LOG.warning("Server not reachable");
            return null;
        }
    }

    public static ClientResponse getAverageAirPollutionLevelsFromTimestamp(Client client, Long t1, Long t2) {
        WebResource webResource = client.resource(SERVER_ADDRESS + "/admin_client/average_pollution_level/"+ t1 + "/" + t2);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            LOG.warning("Server not reachable");
            return null;
        }
    }
}
