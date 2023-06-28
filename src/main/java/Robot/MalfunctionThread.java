package Robot;

import beans.CleaningRobotData;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;

import java.util.Random;

public class MalfunctionThread extends Thread {
    private static final int DELAY = 10000; // 10 seconds delay
    private static final double MALFUNCTION_PROBABILITY = 0.1; // 10% probability

    private Random random;

    public MalfunctionThread() {
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(DELAY);

                if (random.nextDouble() < MALFUNCTION_PROBABILITY) {
                    System.out.println("Robot malfunction occurred!");
                    CleaningRobotDetails.getInstance().setWaitingForMaintenance(true);
                    handleMalfunction();
                }
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted: " + e.getMessage());
                break;
            }
        }
    }

    private void handleMalfunction() {
        long timestamp = System.currentTimeMillis();
        if (CleaningRobotDetails.getInstance().getRobots().size() > 0) {
            SimpleLatch latch = new SimpleLatch(CleaningRobotDetails.getInstance().getRobots().size());
            System.out.println("Numero di robot a cui richiedere l'accesso: " + CleaningRobotDetails.getInstance().getRobots().size());
            RobotServiceOuterClass.MechanicAccessRequest request = RobotServiceOuterClass.MechanicAccessRequest.newBuilder()
                    .setId(CleaningRobotDetails.getInstance().getRobotInfo().getId())
                    .setTimestamp(timestamp)
                    .build();
            for (CleaningRobotData otherRobot : CleaningRobotDetails.getInstance().getRobots()) {
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

    private void occupyMechanic() {
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
    }
}
