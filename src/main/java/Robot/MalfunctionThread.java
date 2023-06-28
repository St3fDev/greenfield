package Robot;

import beans.CleaningRobotData;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MalfunctionThread extends Thread {
    private static final int DELAY = 10000; // 10 seconds delay
    private static final double MALFUNCTION_PROBABILITY = 0.1; // 10% probability
    private static final Object lock = new Object();
    private Random random;

    public MalfunctionThread() {
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(DELAY);
                synchronized (lock) {
                    while (CleaningRobotDetails.getInstance().isInMaintenance()) {
                        lock.wait();
                    }
                }
                if (random.nextDouble() < MALFUNCTION_PROBABILITY) {
                    System.out.println("Robot malfunction occurred!");
                    List<CleaningRobotData> robotSnapshot = CleaningRobotDetails.getInstance().getRobots();
                    handleMalfunction(robotSnapshot);
                }
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted: " + e.getMessage());
                break;
            }
        }
    }

    //TODO TUTTO BUGGATO HAHAHAH
    public static void handleMalfunction(List<CleaningRobotData> robotSnapshot) throws InterruptedException {
        CleaningRobotDetails.getInstance().setWaitingForMaintenance(true);
        if (robotSnapshot.size() > 0) {
            SimpleLatch latch = new SimpleLatch(CleaningRobotDetails.getInstance().getRobots().size());
            System.out.println("Numero di robot a cui richiedere l'accesso: " + CleaningRobotDetails.getInstance().getRobots().size());
            RobotServiceOuterClass.MechanicAccessRequest request = RobotServiceOuterClass.MechanicAccessRequest.newBuilder()
                    .setId(CleaningRobotDetails.getInstance().getRobotInfo().getId())
                    .setTimestamp(CleaningRobotDetails.getInstance().getTimestamp())
                    .build();
            for (CleaningRobotData otherRobot : robotSnapshot) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(5000);
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
                            latch.countDown();
                        }
                        @Override
                        public void onCompleted() {
                            channel.shutdownNow();
                        }
                    });
                    try {
                        channel.awaitTermination(10, TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            occupyMechanic();
        }
        else {
            occupyMechanic();
        }
    }

    private static void occupyMechanic() {
        System.out.println("Occupying mechanic...");
        CleaningRobotDetails.getInstance().setInMaintenance(true);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.err.println("Sleep interrupted: " + e.getMessage());
        }
        System.out.println("Maintenance completed.");
        synchronized (CleaningRobotDetails.getInstance().getLock()) {
            CleaningRobotDetails.getInstance().setWaitingForMaintenance(false);
            CleaningRobotDetails.getInstance().setInMaintenance(false);
            CleaningRobotDetails.getInstance().getLock().notifyAll();
        }
        synchronized (lock) {
            lock.notify();
        }
    }
}
