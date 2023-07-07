import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import common.CleaningRobotData;
import common.RESTMethods;
import common.RobotListResponse;
import common.IOManager;

import java.util.Objects;
import java.util.Scanner;

public class AdministratorClient {

    private static final Client CLIENT = Client.create();

    private static void getRobotList() {
        ClientResponse clientResponse = RESTMethods.showCurrentListCleaningRobot(CLIENT);
        if (Objects.requireNonNull(clientResponse).getStatus() == 200) {
            RobotListResponse robots = clientResponse.getEntity(RobotListResponse.class);
            System.out.println("Cleaning robots currently available in greenfield:");
            for (CleaningRobotData c : robots.getRobots()) {
                IOManager.printRobot(c);
            }
        } else {
            System.out.println(clientResponse.getEntity(String.class));
        }
    }

    private static void getAverageFromRobot() {
        String id = IOManager.insertRobotId();
        int value = IOManager.insertNumberOfMeasurement();
        ClientResponse clientResponse = RESTMethods.getLastNAveragePollutionLevelOfRobot(CLIENT, id, value);
        if (Objects.requireNonNull(clientResponse).getStatus() == 200) {
            String average = clientResponse.getEntity(String.class);
            System.out.println("The average of the last " + value + " air pollution levels measured by the robot [" + id + "] is: " + average);
        } else {
            System.out.println(clientResponse.getEntity(String.class));
        }
    }

    private static void getAverageFromT1ToT2() {
        long t1 = IOManager.insertTimestamp("Enter the first timestamp:");
        long t2 = IOManager.insertTimestamp("Enter the second timestamp:");
        ClientResponse clientResponse = RESTMethods.getAverageAirPollutionLevelsFromTimestamp(CLIENT, t1, t2);
        if (Objects.requireNonNull(clientResponse).getStatus() == 200) {
            String averageFromT1ToT2 = clientResponse.getEntity(String.class);
            System.out.println("The average of air pollution levels measured between " + t1 + " and " + t2 + " is: " + averageFromT1ToT2);
        } else {
            System.out.println(clientResponse.getEntity(String.class));
        }
    }

    public static void main(String[] args) {

        IOManager.printLogo();
        IOManager.printOptions();

        Scanner in = new Scanner(System.in);
        label:
        do {
            System.out.print("> ");
            String option = in.next();
            switch (option) {
                case "0":
                    System.out.println("See you soon!");
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
            System.out.println("-------------------------------------------------------------------------------------------------------------------");
        } while (true);
    }
}
