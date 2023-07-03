package robot.Threads;

import common.CleaningRobotData;
import common.RESTMethods;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import robot.beans.CleaningRobotDetails;

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
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<CleaningRobotData> snapshotRobot = CleaningRobotDetails.getInstance().getRobots();
            final boolean[] isCrashed = {false};
            RobotServiceOuterClass.Empty request = RobotServiceOuterClass.Empty.newBuilder().build();
            for (CleaningRobotData otherRobot : snapshotRobot) {
                Thread thread = new Thread(() -> {
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                    RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
                    stub.heartbeatService(request, new StreamObserver<RobotServiceOuterClass.HeartbeatResponse>(){

                        @Override
                        public void onNext(RobotServiceOuterClass.HeartbeatResponse response) {
                            //System.out.println("Heartbeat received from robot: " + response.getId());
                        }

                        @Override
                        public void onError(Throwable t) {
                            isCrashed[0] = true;
                            System.out.println("[" + getName() + "] " +"Removed robot " + otherRobot.getId() + " because it was unreachable");
                            channel.shutdownNow();
                        }

                        @Override
                        public void onCompleted() {
                            channel.shutdownNow();
                        }
                    });
                    try {
                        channel.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (isCrashed[0]) {
                        System.out.println("In removing...");
                        RESTMethods.deleteRequest(otherRobot.getId());
                        CleaningRobotDetails.getInstance().removeRobot(otherRobot.getId());
                        isCrashed[0] = false;
                    }
                });
                thread.start();
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
