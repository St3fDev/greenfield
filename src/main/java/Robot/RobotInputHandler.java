package Robot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;

import java.util.Scanner;

public class RobotInputHandler extends Thread {
    private static final String SERVER_ADDRESS = "http://localhost:1337";
    private final CleaningRobotData robot;

    public RobotInputHandler(CleaningRobotData robot) {
        this.robot = robot;
    }

    @Override
    public void run() {
        System.out.println("Digit 'exit' to remove the robot from Greenfield");
        System.out.println("Digit 'fix' to send robot to mechanic");
        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (!input.equalsIgnoreCase("quit")) {
            /*if (input.equalsIgnoreCase("fix")) {

            }*/
            input = scanner.nextLine();
        }
        Client client = Client.create();
        System.out.println("Removing the robot from Greenfield");
        String removePath = SERVER_ADDRESS + "/robots/removeRobot/" + robot.getId();
        deleteRequest(client, removePath);
        RobotServiceOuterClass.RobotExitNotification exitRequest = RobotServiceOuterClass.RobotExitNotification.newBuilder().setId(robot.getId()).build();
        if (robot.getRobots().size() > 0) {
            for (CleaningRobotData otherRobot : robot.getRobots()) {
                ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                RobotServiceGrpc.RobotServiceBlockingStub stub = RobotServiceGrpc.newBlockingStub(channel);
                stub.notifyExit(exitRequest);
                channel.shutdownNow();
            }
        }
        System.exit(0);

    }

    private static void deleteRequest(Client client, String url) {
        WebResource webResource = client.resource(url);
        try {
            webResource.type("application/json").delete(ClientResponse.class);
        } catch (ClientHandlerException e){
            System.out.println("Server unavailable");
        }
    }
}
