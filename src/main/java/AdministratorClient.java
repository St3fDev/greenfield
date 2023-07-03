import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import common.CleaningRobotData;
import common.RESTMethods;
import common.RobotListResponse;

import java.util.Scanner;

public class AdministratorClient {

    private static final Client CLIENT = Client.create();

    private static void printOptions() {
        System.out.println(" ________  ________  _______   _______   ________   ________ ___  _______   ___       ________");
        System.out.println("|\\   ____\\|\\   __  \\|\\  ___ \\ |\\  ___ \\ |\\   ___  \\|\\  _____\\\\  \\|\\  ___ \\ |\\  \\     |\\   ___ \\");
        System.out.println("\\ \\  \\  __\\ \\   _  _\\ \\  \\_|/_\\ \\  \\_|/_\\ \\  \\\\ \\  \\ \\   __\\\\ \\  \\ \\  \\_|/_\\ \\  \\    \\ \\  \\ \\\\ \\");
        System.out.println(" \\ \\  \\|\\  \\ \\  \\\\  \\\\ \\  \\_|\\ \\ \\  \\_|\\ \\ \\  \\\\ \\  \\ \\  \\_| \\ \\  \\ \\  \\_|\\ \\ \\  \\____\\ \\  \\_\\\\ \\");
        System.out.println("  \\ \\_______\\ \\__\\\\ _\\\\ \\_______\\ \\_______\\ \\__\\\\ \\__\\ \\__\\   \\ \\__\\ \\_______\\ \\_______\\ \\_______\\");
        System.out.println("   \\|_______|\\|__|\\|__|\\|_______|\\|_______|\\|__| \\|__|\\|__|    \\|__|\\|_______|\\|_______|\\|_______|");
        System.out.println();
        System.out.println("-------------------------------------------------------------------------------------------------------------------");
        System.out.println("|  Welcome in Greenfield!!                                                                                        |");
        System.out.println("|  Digit a number to get the following information:                                                               |");
        System.out.println("|  [1]: shows the list of cleaning robots in Greenfield                                                           |");
        System.out.println("|  [2]: shows the average of the last n air pollution levels measured by a given cleaning robot                   |");
        System.out.println("|  [3]: shows the average of air pollution levels measured between two times                                      |");
        System.out.println("|  [4]: shows all available options                                                                               |");
        System.out.println("|  [0]: quit this program                                                                                         |");
        System.out.println("-------------------------------------------------------------------------------------------------------------------");
    }

    private static void getRobotList() {
        ClientResponse clientResponse = RESTMethods.showCurrentListCleaningRobot(CLIENT);
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

    private static void getAverageFromRobot() {
        Scanner in = new Scanner(System.in);
        System.out.println("Type the robot id:");
        System.out.print("> ");
        String id = in.next();
        System.out.println("Type the number of pollution levels:");
        System.out.print("> ");
        int value = in.nextInt();
        ClientResponse clientResponse = RESTMethods.getLastNAveragePollutionLevelOfRobot(CLIENT, id, value);
        System.out.println(clientResponse.toString());
        String average = clientResponse.getEntity(String.class);
        System.out.println("The average of the last " + value + " air pollution levels measured by the robot with id " + id + " is: " + average);
    }

    private static void getAverageFromT1ToT2() {
        Scanner in = new Scanner(System.in);
        System.out.println("Digit the first timestamp:");
        System.out.print("> ");
        Long t1 = Long.parseLong(in.next());
        System.out.println("Digit the second timestamp:");
        System.out.print("> ");
        Long t2 = Long.parseLong(in.next());
        ClientResponse clientResponse = RESTMethods.getAverageAirPollutionLevelsFromTimestamp(CLIENT, t1, t2);
        System.out.println(clientResponse.toString());
        String averageFromT1ToT2 = clientResponse.getEntity(String.class);
        System.out.println("The average of air pollution levels measured between the interval " + t1 + " and the interval " + t2 +" is: " + averageFromT1ToT2);
    }

    public static void main(String[] args) {

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
                    getRobotList();
                    break;
                case "2":
                    getAverageFromRobot();
                    break;
                case "3":
                    getAverageFromT1ToT2();
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
