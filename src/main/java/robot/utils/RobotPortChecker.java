package robot.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class RobotPortChecker {
    public static boolean isPortOccupied(String address, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // La porta è disponibile
            return false;
        } catch (IOException e) {
            // La porta è occupata
            return true;
        }
    }
}
