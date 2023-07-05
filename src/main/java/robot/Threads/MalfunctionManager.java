package robot.Threads;

import common.CleaningRobotData;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import robot.beans.CleaningRobotModel;
import robot.utils.SimpleLatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MalfunctionManager extends Thread {
    private static final int DELAY = 10000; // 10 seconds delay
    private static final double MALFUNCTION_PROBABILITY = 0.1; // 10% probability
    private static final Object lock = new Object();
    private Random random;
    private volatile boolean stopCondition = false;

    public MalfunctionManager() {

        setName("MalfunctionManager");
        this.random = new Random();
    }

    @Override
    public void run() {
        while (!stopCondition) {
            try {
                Thread.sleep(DELAY);
                synchronized (CleaningRobotModel.getInstance().getLock()) {
                    while (CleaningRobotModel.getInstance().isWaitingForMaintenance()) {
                        CleaningRobotModel.getInstance().getLock().wait();
                    }
                }

                if (random.nextDouble() < MALFUNCTION_PROBABILITY) {
                    System.out.println("Robot malfunction occurred!");
                    List<CleaningRobotData> robotSnapshot = CleaningRobotModel.getInstance().getRobots();
                    handleMalfunction(robotSnapshot);
                }
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted: " + e.getMessage());
                break;
            }
        }
        System.out.println("--------------- MALFUNCTION MANAGER CLOSED --------------");
    }

    public static void handleMalfunction(List<CleaningRobotData> robotSnapshot) throws InterruptedException {
        CleaningRobotModel.getInstance().setWaitingForMaintenance(true);
        CleaningRobotModel.getInstance().setTimestamp(System.currentTimeMillis());
        if (robotSnapshot.size() > 0) {
            SimpleLatch latch = new SimpleLatch(CleaningRobotModel.getInstance().getRobots().size());
            System.out.println("\"OK\" needed to access the mechanic: " + CleaningRobotModel.getInstance().getRobots().size());
            RobotServiceOuterClass.MechanicAccessRequest request = RobotServiceOuterClass.MechanicAccessRequest.newBuilder()
                    .setId(CleaningRobotModel.getInstance().getRobotInfo().getId())
                    .setTimestamp(CleaningRobotModel.getInstance().getTimestamp())
                    .build();
            List<Thread> pool = new ArrayList<>();
            for (CleaningRobotData otherRobot : robotSnapshot) {
                Thread thread = new Thread(() -> {
                    //TODO: da rimuovere: utilizzato solo per scopi di debug
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                    RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
                    stub.accessToMechanic(request, new StreamObserver<RobotServiceOuterClass.MechanicAccessResponse>() {

                        @Override
                        public void onNext(RobotServiceOuterClass.MechanicAccessResponse response) {
                            System.out.println(response.getAck());
                            latch.countDown();
                        }
                        @Override
                        public void onError(Throwable t) {
                            System.out.println("Robot " + otherRobot.getId() + " unreachable: I assume an OK");
                            latch.countDown();
                            channel.shutdown();
                        }
                        @Override
                        public void onCompleted() {
                            channel.shutdown();
                        }
                    });
                    try {
                        if (!channel.awaitTermination(5, TimeUnit.MINUTES)) {
                            channel.shutdown();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                pool.add(thread);
                thread.start();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Thread t:
                 pool) {
                t.join();
            }
            occupyMechanic();
        }
        else {
            occupyMechanic();
        }
    }

    private static void occupyMechanic() {
        System.out.println("Occupying mechanic...");
        CleaningRobotModel.getInstance().setInMaintenance(true);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.err.println("Sleep interrupted: " + e.getMessage());
        }
        System.out.println("Maintenance completed.");
        synchronized (CleaningRobotModel.getInstance().getLock()) {
            CleaningRobotModel.getInstance().setWaitingForMaintenance(false);
            CleaningRobotModel.getInstance().setInMaintenance(false);
            CleaningRobotModel.getInstance().getLock().notifyAll();
        }
    }

    public void stopMeGently() {
        stopCondition = true;
    }
}
