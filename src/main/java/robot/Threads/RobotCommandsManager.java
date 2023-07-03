package robot.Threads;

import common.CleaningRobotData;
import common.RESTMethods;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import robot.MQTT.RobotMqttPublisher;
import robot.beans.CleaningRobotDetails;
import robot.simulators.PM10Simulator;

import java.util.List;
import java.util.Scanner;

public class RobotCommandsManager extends Thread {
    private static final String SERVER_ADDRESS = "http://localhost:1337";
    MalfunctionManager mt;

    List<Thread> threadsToStop;

    public RobotCommandsManager(List<Thread> threadsToStop) {
        this.threadsToStop = threadsToStop;
        mt = new MalfunctionManager();
        setName("RobotInputThread");
    }

    @Override
    public void run() {
        System.out.println("Digit 'quit' to remove the robot from Greenfield");
        System.out.println("Digit 'fix' to send robot to mechanic");
        Scanner scanner = new Scanner(System.in);
        String input = "";
        boolean firstIteration = true; // Flag per la prima iterazione

        while (!input.equalsIgnoreCase("quit")) {
            if (firstIteration) {
                firstIteration = false;
                input = scanner.nextLine();
                continue; // Salta l'iterazione iniziale
            }
            if (input.equalsIgnoreCase("fix")) {
                try {
                    List<CleaningRobotData> robotSnapshot = CleaningRobotDetails.getInstance().getRobots();
                    if (!CleaningRobotDetails.getInstance().isWaitingForMaintenance()) {
                        MalfunctionManager.handleMalfunction(robotSnapshot);
                    } else {
                        System.out.println("The robot is already requiring access from the mechanic or is already in maintenance");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Option not supported");
            }
            input = scanner.nextLine();
        }

        System.out.println("Removing the robot from Greenfield");
        RESTMethods.deleteRequest(CleaningRobotDetails.getInstance().getRobotInfo().getId());
        List<CleaningRobotData> snapshotRobot = CleaningRobotDetails.getInstance().getRobots();
        if (snapshotRobot.size() > 0) {
            RobotServiceOuterClass.RobotExitRequest exitRequest = RobotServiceOuterClass.RobotExitRequest.newBuilder().setId(CleaningRobotDetails.getInstance().getRobotInfo().getId()).build();
            for (CleaningRobotData otherRobot : snapshotRobot) {
                ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
                stub.notifyExit(exitRequest, new StreamObserver<RobotServiceOuterClass.RobotExitResponse>() {

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
                if (thread instanceof RobotMqttPublisher) {
                    ((RobotMqttPublisher) thread).stopMeGently();
                } else if (thread instanceof MalfunctionManager) {
                    ((MalfunctionManager) thread).stopMeGently();
                } else if (thread instanceof PM10Simulator) {
                    ((PM10Simulator) thread).stopMeGently();
                } else if (thread instanceof PM10Consumer) {
                    ((PM10Consumer) thread).stopMeGently();
                } else if (thread instanceof HeartbeatManager) {
                    ((HeartbeatManager) thread).stopMeGently();
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
