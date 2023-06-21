package Robot;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;


public class RobotPresentationThread extends Thread{
    CleaningRobotData robot;
    CleaningRobotData destinationRobot;

    public RobotPresentationThread(CleaningRobotData robot, CleaningRobotData destinationRobot) {
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
                System.out.println("I'm in position: " + robotResponse.getPosition() + " " + robotResponse.getStatus());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage().equals("ERRORE ERRORE"));
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });
    }
}
