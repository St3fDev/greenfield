import common.RESTMethods;
import common.CleaningRobotData;
import common.RobotListResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.util.Scanner;

public class AdministratorClient {

    private static final Client CLIENT = Client.create();

    private static void printOptions() {
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.println("|  Welcome in Greenfield!!                                                                                     |");
        System.out.println("|  Choose a number to get the following information:                                                           |");
        System.out.println("|  1: show the list of cleaning robots currently in Greenfield                                                 |");
        System.out.println("|  2: show the average of the last n air pollution levels recorded by a given cleaning robot                   |");
        System.out.println("|  3: show the average of air pollution levels sent by all robots and occurred between two times               |");
        System.out.println("|  4: show all available options                                                                               |");
        System.out.println("|  0: quit this program                                                                                        |");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
    }

    private static void getRobotList(ClientResponse clientResponse) {
        clientResponse = RESTMethods.showCurrentListCleaningRobot(CLIENT);
        System.out.println(clientResponse.toString());
        RobotListResponse robots = clientResponse.getEntity(RobotListResponse.class);
        System.out.println("these are the ids of the cleaning robots currently in greenfield");
        for (CleaningRobotData c : robots.getRobots()) {
            System.out.println("ID: " + c.getId() + " DISCRICT: " + (c.getDistrict()));
        }
        if (robots.getRobots().isEmpty()) {
            System.out.println("There are no robots in Greenfield, come back later");
        }
    }

    private static void getAverageFromRobot(ClientResponse clientResponse) {
        Scanner in = new Scanner(System.in);
        System.out.println("Type the robot id:");
        System.out.print("> ");
        String id = in.next();
        System.out.println("Type the number of pollution levels:");
        System.out.print("> ");
        int value = in.nextInt();
        clientResponse = RESTMethods.getLastNAveragePollutionLevelOfRobot(CLIENT, id, value);
        System.out.println(clientResponse.toString());
        String average = clientResponse.getEntity(String.class);
        System.out.println("The average of the last " + value + " air pollution levels measured by the robot with id " + id + " is: " + average);
    }

    private static void getAverageFromT1ToT2(ClientResponse clientResponse) {
        Scanner in = new Scanner(System.in);
        System.out.println("Digit the first timestamp:");
        System.out.print("> ");
        Long t1 = Long.parseLong(in.next());
        System.out.println("Digit the second timestamp:");
        System.out.print("> ");
        Long t2 = Long.parseLong(in.next());
        clientResponse = RESTMethods.getAverageAirPollutionLevelsFromTimestamp(CLIENT, t1, t2);
        System.out.println(clientResponse.toString());
        String averageFromT1ToT2 = clientResponse.getEntity(String.class);
        System.out.println("The average of air pollution levels measured between the interval " + t1 + " and the interval " + t2 +" is: " + averageFromT1ToT2);
    }

    public static void main(String[] args) {
        ClientResponse clientResponse = null;

        printOptions();

        Scanner in = new Scanner(System.in);
        label:
        do {
            System.out.print("> ");
            String option = in.next();
            switch (option) {
                case "0":
                    break label;
                case "1":
                    getRobotList(clientResponse);
                    break;
                case "2":
                    getAverageFromRobot(clientResponse);
                    break;
                case "3":
                    getAverageFromT1ToT2(clientResponse);
                    break;
                case "4":
                    printOptions();
                    break;
                default:
                    System.out.println("Option not supported");
                    break;
            }
        } while(true);
    }
}
