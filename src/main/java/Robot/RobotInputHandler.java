package Robot;

import MQTT.RobotMqttSensorPublisher;
import beans.CleaningRobotData;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import simulators.PM10Simulator;
import simulators.Simulator;

import java.util.List;
import java.util.Scanner;

public class RobotInputHandler extends Thread {
    private static final String SERVER_ADDRESS = "http://localhost:1337";
    MalfunctionThread mt;

    List<Thread> threadsToStop;

    public RobotInputHandler(List<Thread> threadsToStop) {
        this.threadsToStop = threadsToStop;
        mt = new MalfunctionThread();
    }

    @Override
    public void run() {
        System.out.println("Digit 'exit' to remove the robot from Greenfield");
        System.out.println("Digit 'fix' to send robot to mechanic");
        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (!input.equalsIgnoreCase("quit")) {
            if (input.equalsIgnoreCase("fix")) {
                try {
                    List<CleaningRobotData> robotSnapshot = CleaningRobotDetails.getInstance().getRobots();
                    if (!CleaningRobotDetails.getInstance().isWaitingForMaintenance()) {
                        MalfunctionThread.handleMalfunction(robotSnapshot);
                    } else {
                        System.out.println("the robot is already requiring access from the mechanic or is already in maintenance");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            input = scanner.nextLine();
        }
        Client client = Client.create();
        System.out.println("Removing the robot from Greenfield");
        String removePath = SERVER_ADDRESS + "/robots/removeRobot/" + CleaningRobotDetails.getInstance().getRobotInfo().getId();
        deleteRequest(client, removePath);
        if (CleaningRobotDetails.getInstance().getRobots().size() > 0) {
            //TODO capire cosa succede se invia la richiesta di uscita ad un robot crashato
            RobotServiceOuterClass.RobotExitRequest exitRequest = RobotServiceOuterClass.RobotExitRequest.newBuilder().setId(CleaningRobotDetails.getInstance().getRobotInfo().getId()).build();
            for (CleaningRobotData otherRobot : CleaningRobotDetails.getInstance().getRobots()) {
                ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
                stub.notifyExit(exitRequest, new StreamObserver<RobotServiceOuterClass.RobotExitResponse>(){

                    @Override
                    public void onNext(RobotServiceOuterClass.RobotExitResponse response) {
                        System.out.println("successfully removed from the robot: " + response.getId());
                    }

                    @Override
                    public void onError(Throwable t) {
                        channel.shutdownNow();
                    }

                    @Override
                    public void onCompleted() {
                        channel.shutdownNow();
                    }
                });
            }
        }
        shutdown(threadsToStop);
    }

    private void shutdown(List<Thread> threadsToStop) {
        for (Thread thread : threadsToStop) {
            if (thread.isAlive()) {
                if (thread instanceof RobotMqttSensorPublisher) {
                    ((RobotMqttSensorPublisher) thread).stopMeGently();
                } else if (thread instanceof MalfunctionThread) {
                    ((MalfunctionThread) thread).stopMeGently();
                } else if (thread instanceof PM10Simulator) {
                    ((PM10Simulator) thread).stopMeGently();
                } else if (thread instanceof PM10Consumer) {
                    ((PM10Consumer) thread).stopMeGently();
                }
            }
        }
        for (Thread thread : threadsToStop) {
            if (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("exit completed!");
        System.exit(0);
    }

    private static void deleteRequest(Client client, String url) {
        WebResource webResource = client.resource(url);
        try {
            webResource.type("application/json").delete(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server unavailable");
        }
    }
}
