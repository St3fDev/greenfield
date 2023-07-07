package common;

import common.CleaningRobotData;
import robot.utils.RobotPortChecker;

import java.util.Scanner;

public class IOManager {


    public static String setAddress() {
        String robotAddress;
        Scanner in = new Scanner(System.in);
        System.out.println("Digit the address of the cleaning robot:");
        System.out.print("> ");
        robotAddress = in.next();
        return robotAddress;
    }

    public static int setPort(String robotAddress) {
        boolean validPort = false;
        int robotPort = 0;
        Scanner in = new Scanner(System.in);
        do {
            try {
                System.out.println("Digit the port of the cleaning robot:");
                System.out.print("> ");
                robotPort = Integer.parseInt(in.nextLine());

                if (robotPort > 0) {
                    validPort = true;

                    if (RobotPortChecker.isPortOccupied(robotAddress, robotPort)) {
                        System.out.println("The specified port is already occupied. Please choose another port.");
                        validPort = false;
                    }
                } else {
                    System.out.println("Invalid input. Please digit a positive integer for the port.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please digit a valid integer for the port.");
            }
        } while (!validPort);

        return robotPort;
    }

    public static String insertRobotId() {
        Scanner in = new Scanner(System.in);
        System.out.println("Digit the cleaning robot id:");
        System.out.print("> ");
        return in.next();
    }

    public static int insertNumberOfMeasurement() {
        Scanner in = new Scanner(System.in);
        int value = 0;
        boolean validInput = false;
        while (!validInput) {
            System.out.println("Digit the number of pollution levels:");
            System.out.print("> ");
            if (in.hasNextInt()) {
                value = in.nextInt();
                if (value > 0) {
                    validInput = true;
                } else {
                    System.out.println("Invalid input. Please digit a positive number.");
                }
            } else {
                System.out.println("Invalid input. Please digit a valid number.");
                in.next();
            }
        }
        return value;
    }

    public static long insertTimestamp(String prompt) {
        Scanner in = new Scanner(System.in);
        long timestamp = 0;
        boolean validTimestamp = false;
        while (!validTimestamp) {
            System.out.println(prompt);
            System.out.print("> ");
            String input = in.next();
            try {
                timestamp = Long.parseLong(input);
                validTimestamp = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please digit a valid long for the timestamp.");
            }
        }
        return timestamp;
    }

    public static void printRobot(CleaningRobotData cleaningRobot) {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("|           " + cleaningRobot + "           |");
        System.out.println("--------------------------------------------------------------------------");
    }

    public static void printOptions() {
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

    public static void printLogo() {
        System.out.println(" ________  ________  _______   _______   ________   ________ ___  _______   ___       ________");
        System.out.println("|\\   ____\\|\\   __  \\|\\  ___ \\ |\\  ___ \\ |\\   ___  \\|\\  _____\\\\  \\|\\  ___ \\ |\\  \\     |\\   ___ \\");
        System.out.println("\\ \\  \\  __\\ \\   _  _\\ \\  \\_|/_\\ \\  \\_|/_\\ \\  \\\\ \\  \\ \\   __\\\\ \\  \\ \\  \\_|/_\\ \\  \\    \\ \\  \\ \\\\ \\");
        System.out.println(" \\ \\  \\|\\  \\ \\  \\\\  \\\\ \\  \\_|\\ \\ \\  \\_|\\ \\ \\  \\\\ \\  \\ \\  \\_| \\ \\  \\ \\  \\_|\\ \\ \\  \\____\\ \\  \\_\\\\ \\");
        System.out.println("  \\ \\_______\\ \\__\\\\ _\\\\ \\_______\\ \\_______\\ \\__\\\\ \\__\\ \\__\\   \\ \\__\\ \\_______\\ \\_______\\ \\_______\\");
        System.out.println("   \\|_______|\\|__|\\|__|\\|_______|\\|_______|\\|__| \\|__|\\|__|    \\|__|\\|_______|\\|_______|\\|_______|");
        System.out.println();
    }

}
