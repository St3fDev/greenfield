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

    /*
    quando il robot manda l'ok:
	    quando non è interessato ad andare dal meccanico
	    quando è interessato ma il suo timestamp è maggiore di quello dell'altro robot che ha fatto la richiesta
    Quando non manda l'ok:
	    quando è già dal meccanico
	    quando il suo timestamp è minore del timestamp dell'altro robot che ha fatto la richiesta
     */
    @Override
    public void accessToMechanic(RobotServiceOuterClass.MechanicAccessRequest request, StreamObserver<RobotServiceOuterClass.MechanicAccessResponse> responseObserver) {
        synchronized (CleaningRobotDetails.getInstance().getLock()) {
            while ((CleaningRobotDetails.getInstance().isWaitingForMaintenance() &&
                    CleaningRobotDetails.getInstance().checkTimestamp(request.getTimestamp())) ||
                    CleaningRobotDetails.getInstance().isInMaintenance()) {
                try {
                    CleaningRobotDetails.getInstance().getLock().wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!CleaningRobotDetails.getInstance().isWaitingForMaintenance() && !CleaningRobotDetails.getInstance().isInMaintenance()) {
                RobotServiceOuterClass.MechanicAccessResponse response = RobotServiceOuterClass.MechanicAccessResponse.newBuilder()
                        .setAck("OK")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }
    }


}
