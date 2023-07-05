package robot.Threads;

import common.CleaningRobotData;
import common.RESTMethods;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import robot.beans.CleaningRobotModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HeartbeatManager extends Thread{

    private static final Logger LOG = Logger.getLogger(HeartbeatManager.class.getName());
    private volatile boolean stopCondition = false;
    public HeartbeatManager() {
        setName("HeartbeatManager");
    }

    @Override
    public void run() {
        while(!stopCondition) {
            synchronized (CleaningRobotModel.getInstance().getSizeListLock()) {
                while(CleaningRobotModel.getInstance().getRobots().size() < 1 && !stopCondition) {
                    try {
                        CleaningRobotModel.getInstance().getSizeListLock().wait();
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
            List<CleaningRobotData> snapshotRobot = CleaningRobotModel.getInstance().getRobots();
            RobotServiceOuterClass.Empty request = RobotServiceOuterClass.Empty.newBuilder().build();
            List<Thread> pool = new ArrayList<>();
            for (CleaningRobotData otherRobot : snapshotRobot) {
                Thread thread = new Thread(() -> {
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(otherRobot.getAddress() + ":" + otherRobot.getPort()).usePlaintext(true).build();
                    RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);

                    final boolean[] didAnswer = {false};

                    stub.heartbeatService(request, new StreamObserver<RobotServiceOuterClass.HeartbeatResponse>(){

                        @Override
                        public void onNext(RobotServiceOuterClass.HeartbeatResponse response) {
                            didAnswer[0] = true;
                            //System.out.println("Heartbeat received from robot: " + response.getId());
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
                        if (!channel.awaitTermination(2, TimeUnit.SECONDS)) {
                            channel.shutdown();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (!didAnswer[0]) {
                        LOG.warning("[" + getName() + "] " +"Robot " + otherRobot.getId() + " is unreachable. In removing...");
                        RESTMethods.deleteRequest(otherRobot.getId());
                        CleaningRobotModel.getInstance().removeRobot(otherRobot.getId());
                        System.out.println("Robot " + otherRobot.getId() + " Removed from topology");
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
        synchronized (CleaningRobotModel.getInstance().getSizeListLock()) {
            stopCondition = true;
            CleaningRobotModel.getInstance().getSizeListLock().notify();
        }
    }
}
