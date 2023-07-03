package robot.Threads;

import common.CleaningRobotData;
import robot.utils.RobotPortChecker;

import java.util.Scanner;

public class IOManager {

    public static String setAddress() {
        String robotAddress;
        Scanner in = new Scanner(System.in);
        boolean validPort = false;

        System.out.println("Type the address of the robot: ");
        System.out.print("> ");
        robotAddress = in.next();
        return robotAddress;
    }

    public static int setPort(String robotAddress) {
        Scanner in = new Scanner(System.in);
        boolean validPort = false;
        int robotPort = 0;
        while (!validPort) {
            System.out.println("Type the port of the robot: ");
            System.out.print("> ");
            robotPort = Integer.parseInt(in.next());

            validPort = true; // Assume che la porta sia valida

            // Verifica se l'indirizzo o la porta sono già occupati
            if (RobotPortChecker.isPortOccupied(robotAddress, robotPort)) {
                System.out.println("The specified port is already occupied. Please choose another port.");
                validPort = false; // La porta non è valida
            }

            if (!validPort) {
                System.out.println("Invalid address or port. Please try again.");
            }
        }
        return robotPort;
    }


    public static void printRobot(CleaningRobotData cleaningRobot) {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("|             " + cleaningRobot + "              |");
        System.out.println("----------------------------------------------------------------------");
    }
}
