package robot.Threads;

import common.CleaningRobotData;
import robot.utils.RobotPortChecker;

import java.util.Scanner;

public class IOManager {

    public static String setAddress() {
        String robotAddress;
        Scanner in = new Scanner(System.in);

        System.out.println("Type the address of the robot: ");
        System.out.print("> ");
        robotAddress = in.next();
        return robotAddress;
    }

    public static int setPort(String robotAddress) {
        Scanner in = new Scanner(System.in);
        boolean validPort = false;
        int robotPort = 0;

        do {
            try {
                System.out.println("Type the port of the robot: ");
                System.out.print("> ");
                robotPort = Integer.parseInt(in.next());
                validPort = true;

                if (RobotPortChecker.isPortOccupied(robotAddress, robotPort)) {
                    System.out.println("The specified port is already occupied. Please choose another port.");
                    validPort = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer for the port.");
                in.nextLine();
            }
        } while (!validPort);

        return robotPort;
    }

    public static void printRobot(CleaningRobotData cleaningRobot) {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("|             " + cleaningRobot + "              |");
        System.out.println("----------------------------------------------------------------------");
    }

    public static void printOptions() {
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
}
