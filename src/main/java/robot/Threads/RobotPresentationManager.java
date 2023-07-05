package robot.Threads;

import common.CleaningRobotData;
import common.RESTMethods;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import robot.beans.CleaningRobotModel;

import java.util.concurrent.TimeUnit;


public class RobotPresentationManager extends Thread{
    CleaningRobotData robot;
    CleaningRobotData destinationRobot;

    public RobotPresentationManager(CleaningRobotData robot, CleaningRobotData destinationRobot) {
        this.robot = robot;
        this.destinationRobot = destinationRobot;
    }

    public void run() {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(destinationRobot.getAddress() + ":" + destinationRobot.getPort()).usePlaintext(true).build();

        RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);

        RobotServiceOuterClass.RobotPresentation request = RobotServiceOuterClass.RobotPresentation.newBuilder()
                .setId(robot.getId())
                .setAddress(robot.getAddress())
                .setPort(robot.getPort())
                .setPosition(RobotServiceOuterClass.Position.newBuilder()
                        .setX(robot.getPosition().getX())
                        .setY(robot.getPosition().getY())
                        .build())
                .build();

        stub.presentation(request, new StreamObserver<RobotServiceOuterClass.RobotResponse>() {
            @Override
            public void onNext(RobotServiceOuterClass.RobotResponse robotResponse) {
                System.out.println("Welcome from robot: " + robotResponse.getId());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Removed robot " + destinationRobot.getId() + " because it is unreachable");
                RESTMethods.deleteRequest(destinationRobot.getId());
                CleaningRobotModel.getInstance().getRobots().removeIf((elem) -> elem.getId().equals(destinationRobot.getId()));
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
    }
}
