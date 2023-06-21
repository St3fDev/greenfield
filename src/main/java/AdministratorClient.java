import Robot.CleaningRobotData;
import beans.GreenfieldModel;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sun.jersey.api.client.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class AdministratorClient {

    private static void printOptions() {
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.println("|  Welcome in Greenfield!!                                                                                     |");
        System.out.println("|  Choose a number to get the following information:                                                           |");
        System.out.println("|  1: show the list of cleaning robots currently in Greenfield                                                 |");
        System.out.println("|  2: show the average of the last n air pollution levels recorded by a given cleaning robot                   |");
        System.out.println("|  3: show the average of the last n air pollution levels sent by all robots and occurred between two times    |");
        System.out.println("|  4: show all available options                                                                               |");
        System.out.println("|  0: quit this program                                                                                        |");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
    }

    private static ClientResponse showCurrentListCleaningRobot(Client client, String serverAddress) {
        WebResource webResource = client.resource(serverAddress + "/adminClient/getRobots");
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server not available");
            return null;
        }
    }

    private static ClientResponse getLastNAveragePollutionLevelOfRobot(Client client, String serverAddress, String id, int value) {
        WebResource webResource = client.resource(serverAddress + "/adminClient/last_n_avg_pollution/"+ id + "/" + String.valueOf(value));
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server not available");
            return null;
        }
    }

    /*private static ClientResponse getAverageAirPollutionLevelsFromTimestamp(Client client, String serverAddress, Long t1, Long t2) {
        try {

        } catch (ClientHandlerException e) {
            System.out.println("Server not available");
            return null;
        }
    }*/

    public static void main(String[] args) {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337";
        ClientResponse clientResponse = null;

        printOptions();

        Scanner in = new Scanner(System.in);
        do {
            System.out.print("> ");
            String option = in.next();
            if (option.equals("0")) {
                break;
            } else if (option.equals("4")) {
                printOptions();
            } else if (option.equals("1")) {
                clientResponse = showCurrentListCleaningRobot(client, serverAddress);
                System.out.println(clientResponse.toString());
                String robotsJson = clientResponse.getEntity(String.class);
                Type listType = new TypeToken<List<CleaningRobotData>>() {}.getType();
                List<CleaningRobotData> robots = new Gson().fromJson(robotsJson, listType);
                System.out.println("these are the ids of the cleaning robots currently in greenfield");
                for (CleaningRobotData c: robots) {
                    System.out.println("ID: " + c.getId() + " DISCRICT: " + (c.getDistrict()));
                }
                if (robots.isEmpty()){
                    System.out.println("There are no robots in Greenfield, come back later");
                }
            } else if (option.equals("2")){
                System.out.println("Type the robot id:");
                System.out.print("> ");
                String id = in.next();
                System.out.println("Type the number of pollution levels:");
                System.out.print("> ");
                int value = in.nextInt();
                clientResponse = getLastNAveragePollutionLevelOfRobot(client, serverAddress, id, value);
                System.out.println(clientResponse.toString());
                String average = clientResponse.getEntity(String.class);
                System.out.println("The average of the last " + value + " air pollution levels measured by the robot with id "+ id +" are: " + average);
            } else {

            }
        } while(true);
    }
}
