package GRPC;

import Robot.CleaningRobotDetails;
import beans.CleaningRobotData;
import beans.Position;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import it.robot.grpc.RobotServiceOuterClass.RobotPresentation;
import it.robot.grpc.RobotServiceOuterClass.RobotResponse;
import it.robot.grpc.RobotServiceOuterClass.Status;

public class RobotServiceImpl extends RobotServiceGrpc.RobotServiceImplBase {

    public RobotServiceImpl(){}

    @Override
    public void presentation(RobotPresentation request, StreamObserver<RobotResponse> responseObserver) {
        RobotResponse robotResponse = RobotResponse.newBuilder()
                .setId(CleaningRobotDetails.getInstance().getRobotInfo().getId())
                .setStatus(Status.OK)
                .build();
        responseObserver.onNext(robotResponse);
        responseObserver.onCompleted();
        System.out.println("> Adding drone "+request.getId()+" to my topology ...");
        CleaningRobotData robotToInsert = new CleaningRobotData(request.getId(), request.getAddress(), request.getPort());
        System.out.println("I'M " + CleaningRobotDetails.getInstance().getRobotInfo().getId());
        robotToInsert.setPosition(new Position(request.getPosition().getX(),request.getPosition().getY()));
        CleaningRobotDetails.getInstance().addRobot(robotToInsert);
    }

    @Override
    public void notifyExit(RobotServiceOuterClass.RobotExitNotification request, StreamObserver<RobotServiceOuterClass.Empty> responseObserver) {
        String idRobotToRemove = request.getId();
        CleaningRobotDetails.getInstance().removeRobot(idRobotToRemove);
        responseObserver.onNext(RobotServiceOuterClass.Empty.newBuilder().build());
        responseObserver.onCompleted();
        System.out.println("Removing robot: " + request.getId() + " to my topology...");
    }

    @Override
    public void accessToMechanic(RobotServiceOuterClass.MechanicAccessRequest request, StreamObserver<RobotServiceOuterClass.MechanicAccessResponse> responseObserver) {

    }


}
