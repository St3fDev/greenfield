package Robot;

import beans.CleaningRobotData;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;

import java.util.List;

public class HeartbeatThread extends Thread{

    private volatile boolean stopCondition = false;
    public HeartbeatThread() {
        setName("HeartbeatThread");
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
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<CleaningRobotData> snapshotRobot = CleaningRobotDetails.getInstance().getRobots();
            RobotServiceOuterClass.Empty request = RobotServiceOuterClass.Empty.newBuilder().build();
            for (CleaningRobotData otherRobot : snapshotRobot) {
                ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
                stub.heartbeatService(request, new StreamObserver<RobotServiceOuterClass.HeartbeatResponse>(){

                    @Override
                    public void onNext(RobotServiceOuterClass.HeartbeatResponse response) {
                        //System.out.println("Heartbeat received from robot: " + response.getId());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Removed robot " + otherRobot.getId() + " because it was unreachable");
                        RESTMethod.deleteRequest(otherRobot.getId());
                        CleaningRobotDetails.getInstance().getRobots().removeIf((elem) -> elem.getId().equals(otherRobot.getId()));
                        channel.shutdownNow();
                    }

                    @Override
                    public void onCompleted() {
                        channel.shutdownNow();
                    }
                });
            }
        }
        System.out.println("---------------- HEARTBEAT THREAD CLOSED -----------------");
    }

    public void stopMeGently() {
        synchronized (CleaningRobotDetails.getInstance().getSizeListLock()) {
            stopCondition = true;
            CleaningRobotDetails.getInstance().getSizeListLock().notify();
        }
    }
}
