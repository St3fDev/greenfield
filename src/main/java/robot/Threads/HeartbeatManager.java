package robot.Threads;

import common.CleaningRobotData;
import common.RESTMethods;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import robot.beans.CleaningRobotDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HeartbeatManager extends Thread{

    private volatile boolean stopCondition = false;
    public HeartbeatManager() {
        setName("HeartbeatManager");
    }

    @Override
    public void run() {
        while(!stopCondition) {
            synchronized (CleaningRobotDetails.getInstance().getSizeListLock()) {
                while(CleaningRobotDetails.getInstance().getRobots().size() < 1 && !stopCondition) {
                    try {
                        CleaningRobotDetails.getInstance().getSizeListLock().wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (stopCondition) break;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<CleaningRobotData> snapshotRobot = CleaningRobotDetails.getInstance().getRobots();
            RobotServiceOuterClass.Empty request = RobotServiceOuterClass.Empty.newBuilder().build();
            List<Thread> pool = new ArrayList<>();
            System.out.println("LANCIO I CAZZO DI THREAD");
            for (CleaningRobotData otherRobot : snapshotRobot) {
                Thread thread = new Thread(() -> {
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                    RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);

                    final boolean[] didAnswer = {false};

                    stub.heartbeatService(request, new StreamObserver<RobotServiceOuterClass.HeartbeatResponse>(){

                        @Override
                        public void onNext(RobotServiceOuterClass.HeartbeatResponse response) {
                            didAnswer[0] = true;
                            System.out.println("Heartbeat received from robot: " + response.getId());
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
                    try {
                        channel.awaitTermination(2, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (!didAnswer[0]) {
                        System.out.println("[" + getName() + "] " +"Removed robot " + otherRobot.getId() + " because it was unreachable");
                        System.out.println("In removing...");
                        RESTMethods.deleteRequest(otherRobot.getId());
                        CleaningRobotDetails.getInstance().removeRobot(otherRobot.getId());
                        didAnswer[0] = false;
                    }
                });
                pool.add(thread);
                thread.start();
            }
            for (Thread t: pool) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("---------------- HEARTBEAT MANAGER CLOSED ---------------");
    }

    public void stopMeGently() {
        synchronized (CleaningRobotDetails.getInstance().getSizeListLock()) {
            stopCondition = true;
            CleaningRobotDetails.getInstance().getSizeListLock().notify();
        }
    }
}
