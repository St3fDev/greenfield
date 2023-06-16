import Robot.CleaningRobotData;
import beans.GreenfieldModel;
import com.sun.jersey.api.client.*;

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

    public static void main(String[] args) {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337";
        ClientResponse clientResponse = null;

        printOptions();

        Scanner in = new Scanner(System.in);
        do {
            String option = in.next();
            if (option.equals("0")) {
                break;
            } else if (option.equals("4")) {
                printOptions();
            } else if (option.equals("1")) {
                clientResponse = showCurrentListCleaningRobot(client, serverAddress);
                System.out.println(clientResponse.toString());
                GreenfieldModel robots = clientResponse.getEntity(GreenfieldModel.class);
                System.out.println("these are the ids of the cleaning robots currently in greenfield");
                for (CleaningRobotData c: robots.getRobots()) {
                    System.out.println("ID: " + c.getId() + " DISCRICT: " + (c.getDistrict()));
                }
                if (robots.getRobots().isEmpty()){
                    System.out.println("There are no robots in Greenfield, come back later");
                }
            } else if (option.equals("2")){

            } else {

            }
        } while(true);
    }
}
