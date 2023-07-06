package robot.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class RobotPortChecker {

    private static final Logger LOG = Logger.getLogger(RobotPortChecker.class.getName());
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
                    LOG.warning(("Error occurred while closing the socket: " + e.getMessage()));
                }
            }
        }
    }
}
