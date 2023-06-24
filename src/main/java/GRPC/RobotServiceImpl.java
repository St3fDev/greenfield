package GRPC;

import Robot.CleaningRobotData;
import beans.Position;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import it.robot.grpc.RobotServiceOuterClass.RobotPresentation;
import it.robot.grpc.RobotServiceOuterClass.RobotResponse;
import it.robot.grpc.RobotServiceOuterClass.Status;

public class RobotServiceImpl extends RobotServiceGrpc.RobotServiceImplBase {

    CleaningRobotData robot;
    public RobotServiceImpl(CleaningRobotData robot) {
        this.robot = robot;
    }

    @Override
    public void presentation(RobotPresentation request, StreamObserver<RobotResponse> responseObserver) {
        RobotResponse robotResponse = RobotResponse.newBuilder()
                .setId(robot.getId())
                .setStatus(Status.OK)
                .build();
        responseObserver.onNext(robotResponse);
        responseObserver.onCompleted();
        System.out.println("> Adding drone "+request.getId()+" to my topology ...");
        CleaningRobotData robotToInsert = new CleaningRobotData(request.getId(), request.getAddress(), request.getPort());
        robotToInsert.setPosition(new Position(request.getPosition().getX(),request.getPosition().getY()));
        robot.addRobot(robotToInsert);
    }

    @Override
    public void notifyExit(RobotServiceOuterClass.RobotExitNotification request, StreamObserver<RobotServiceOuterClass.Empty> responseObserver) {
        String idRobotToRemove = request.getId();
        robot.removeRobot(idRobotToRemove);
        responseObserver.onNext(RobotServiceOuterClass.Empty.newBuilder().build());
        responseObserver.onCompleted();
        System.out.println("Removing robot: " + request.getId() + "to my topology");
    }


}
