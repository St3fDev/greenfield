package robot.Threads;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import common.CleaningRobotData;
import robot.beans.CleaningRobotDetails;
import common.RESTMethods;

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
                System.out.println("I'm robot: " + robotResponse.getId() +" -> " + robotResponse.getStatus());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Removed robot " + destinationRobot.getId() + " because it was unreachable");
                RESTMethods.deleteRequest(destinationRobot.getId());
                CleaningRobotDetails.getInstance().getRobots().removeIf((elem) -> elem.getId().equals(destinationRobot.getId()));
                channel.shutdownNow();
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });
        try {
            channel.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
