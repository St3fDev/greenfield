package robot.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RobotPortChecker {
    public static boolean isPortOccupied(String address, int port) {
        Socket socket = null;
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            socket = new Socket();
            socket.connect(socketAddress, 1000);
            // La porta è occupata
            return true;
        } catch (IOException e) {
            // La porta è disponibile
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
