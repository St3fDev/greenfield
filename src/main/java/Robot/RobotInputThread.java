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

import java.util.List;
import java.util.Scanner;

public class RobotInputThread extends Thread {
    private static final String SERVER_ADDRESS = "http://localhost:1337";
    MalfunctionThread mt;

    List<Thread> threadsToStop;

    public RobotInputThread(List<Thread> threadsToStop) {
        this.threadsToStop = threadsToStop;
        mt = new MalfunctionThread();
        setName("RobotInputThread");
    }

    @Override
    public void run() {
        System.out.println("Digit 'quit' to remove the robot from Greenfield");
        System.out.println("Digit 'fix' to send robot to mechanic");
        Scanner scanner = new Scanner(System.in);
        String input = ""; while (!input.equalsIgnoreCase("quit")) {
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

        System.out.println("Removing the robot from Greenfield");
        RESTMethod.deleteRequest(CleaningRobotDetails.getInstance().getRobotInfo().getId());
        List<CleaningRobotData> snapshotRobot = CleaningRobotDetails.getInstance().getRobots();
        if (snapshotRobot.size() > 0) {
            RobotServiceOuterClass.RobotExitRequest exitRequest = RobotServiceOuterClass.RobotExitRequest.newBuilder().setId(CleaningRobotDetails.getInstance().getRobotInfo().getId()).build();
            for (CleaningRobotData otherRobot : snapshotRobot) {
                ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
                stub.notifyExit(exitRequest, new StreamObserver<RobotServiceOuterClass.RobotExitResponse>(){

                    @Override
                    public void onNext(RobotServiceOuterClass.RobotExitResponse response) {
                        System.out.println("successfully removed from list of the robot: " + response.getId());
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
                } else if(thread instanceof  HeartbeatThread) {
                    ((HeartbeatThread) thread).stopMeGently();
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
}
