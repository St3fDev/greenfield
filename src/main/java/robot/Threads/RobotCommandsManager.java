package robot.Threads;

import com.sun.jersey.api.client.ClientResponse;
import common.CleaningRobotData;
import common.RESTMethods;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import robot.GRPC.RobotGRPCServer;
import robot.MQTT.RobotMqttPublisher;
import robot.beans.CleaningRobotModel;
import robot.simulators.PM10Simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RobotCommandsManager extends Thread {
    private static final Logger LOG = Logger.getLogger(RobotCommandsManager.class.getName());

    List<Thread> threadsToStop;

    public RobotCommandsManager(List<Thread> threadsToStop) {
        this.threadsToStop = threadsToStop;
        setName("RobotCommandsManager");
    }

    @Override
    public void run() {
        System.out.println("Digit 'quit' to remove the cleaning robot from Greenfield");
        System.out.println("Digit 'fix' to send cleaning robot to the mechanic");
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
                    List<CleaningRobotData> robotSnapshot = CleaningRobotModel.getInstance().getRobots();
                    if (!CleaningRobotModel.getInstance().isWaitingForMaintenance()) {
                        MalfunctionManager.handleMalfunction(robotSnapshot);
                    } else {
                        System.out.println("The cleaning robot is already requiring access from the mechanic or is already in maintenance");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Option not supported");
            }
            input = scanner.nextLine();
        }

        LOG.info("Start cleaning robot removal process");
        List<CleaningRobotData> snapshotRobot = CleaningRobotModel.getInstance().getRobots();
        if (snapshotRobot.size() > 0) {
            RobotServiceOuterClass.RobotExitRequest exitRequest = RobotServiceOuterClass.RobotExitRequest.newBuilder().setId(CleaningRobotModel.getInstance().getRobotInfo().getId()).build();
            List<Thread> pool = new ArrayList<>();
            for (CleaningRobotData otherRobot : snapshotRobot) {
                Thread thread = new Thread(() -> {
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                    RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
                    stub.notifyExit(exitRequest, new StreamObserver<RobotServiceOuterClass.RobotExitResponse>() {

                        @Override
                        public void onNext(RobotServiceOuterClass.RobotExitResponse response) {
                            System.out.println("successfully removed from list of the cleaning robot: " + response.getId());
                        }

                        @Override
                        public void onError(Throwable t) {
                            channel.shutdown();
                        }

                        @Override
                        public void onCompleted() {
                            channel.shutdown();
                        }
                    });
                    try {
                        if (!channel.awaitTermination(10, TimeUnit.SECONDS)) {
                            channel.shutdown();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                pool.add(thread);
                thread.start();
            }
            for (Thread t :
                    pool) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
                    LOG.warning("Interrupted while waiting for thread to finish: " + e.getMessage());
                }
            }
        }
        RobotGRPCServer.stopMeGently();
        ClientResponse response = RESTMethods.deleteRequest(CleaningRobotModel.getInstance().getRobotInfo().getId());
        LOG.info(Objects.requireNonNull(response).getEntity(String.class));
        LOG.info("EXIT COMPLETED!");
    }
}
