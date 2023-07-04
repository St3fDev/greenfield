import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import common.CleaningRobotData;
import common.RESTMethods;
import common.RobotListResponse;
import robot.Threads.IOManager;

import java.util.Scanner;

public class AdministratorClient {

    private static final Client CLIENT = Client.create();

    private static void getRobotList() {
        ClientResponse clientResponse = RESTMethods.showCurrentListCleaningRobot(CLIENT);
        System.out.println(clientResponse.toString());
        if (clientResponse.getStatus() == 200) {
            RobotListResponse robots = clientResponse.getEntity(RobotListResponse.class);
            System.out.println("Robot currently in the greenfield:");
            for (CleaningRobotData c : robots.getRobots()) {
                IOManager.printRobot(c);
            }
        } else {
            System.out.println(clientResponse.getEntity(String.class));
        }
    }

    private static void getAverageFromRobot() {
        Scanner in = new Scanner(System.in);
        System.out.println("Type the robot id:");
        System.out.print("> ");
        String id = in.next();
        int value = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("Type the number of pollution levels:");
            System.out.print("> ");
            if (in.hasNextInt()) {
                value = in.nextInt();
                validInput = true;
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                in.next(); // Consumes the invalid input
            }
        }
        ClientResponse clientResponse = RESTMethods.getLastNAveragePollutionLevelOfRobot(CLIENT, id, value);
        System.out.println(clientResponse.toString());
        if (clientResponse.getStatus() == 200) {
            String average = clientResponse.getEntity(String.class);
            System.out.println("The average of the last " + value + " air pollution levels measured by the robot [" + id + "] is: " + average);
        } else {
            System.out.println(clientResponse.getEntity(String.class));
        }
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
        if (clientResponse.getStatus() == 200) {
            String averageFromT1ToT2 = clientResponse.getEntity(String.class);
            System.out.println("The average of air pollution levels measured between " + t1 + " and " + t2 + " is: " + averageFromT1ToT2);
        } else {
            System.out.println(clientResponse.getEntity(String.class));
        }
    }

    public static void main(String[] args) {

        IOManager.printOptions();

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
                    IOManager.printOptions();
                    break;
                default:
                    System.out.println("Option not supported");
                    break;
            }
        } while (true);
    }
}
