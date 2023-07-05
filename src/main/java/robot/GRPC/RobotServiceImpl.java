package robot.GRPC;

import common.CleaningRobotData;
import common.Position;
import io.grpc.stub.StreamObserver;
import it.robot.grpc.RobotServiceGrpc;
import it.robot.grpc.RobotServiceOuterClass;
import it.robot.grpc.RobotServiceOuterClass.RobotPresentation;
import it.robot.grpc.RobotServiceOuterClass.RobotResponse;
import robot.beans.CleaningRobotModel;

public class RobotServiceImpl extends RobotServiceGrpc.RobotServiceImplBase {

    public RobotServiceImpl() {
    }

    @Override
    public void presentation(RobotPresentation request, StreamObserver<RobotResponse> responseObserver) {
        RobotResponse robotResponse = RobotResponse.newBuilder()
                .setId(CleaningRobotModel.getInstance().getRobotInfo().getId())
                .build();
        responseObserver.onNext(robotResponse);
        responseObserver.onCompleted();
        System.out.println("> Adding robot " + request.getId() + " to my topology ...");
        CleaningRobotData robotToInsert = new CleaningRobotData(request.getId(), request.getAddress(), request.getPort());
        robotToInsert.setPosition(new Position(request.getPosition().getX(), request.getPosition().getY()));
        CleaningRobotModel.getInstance().addRobot(robotToInsert);
        synchronized (CleaningRobotModel.getInstance().getSizeListLock()) {
            CleaningRobotModel.getInstance().getSizeListLock().notify();
        }
    }

    @Override
    public void notifyExit(RobotServiceOuterClass.RobotExitRequest request, StreamObserver<RobotServiceOuterClass.RobotExitResponse> responseObserver) {
        String idRobotToRemove = request.getId();
        CleaningRobotModel.getInstance().removeRobot(idRobotToRemove);
        responseObserver.onNext(RobotServiceOuterClass.RobotExitResponse.newBuilder()
                        .setId(CleaningRobotModel.getInstance().getRobotInfo().getId())
                .build());
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
        synchronized (CleaningRobotModel.getInstance().getLock()) {
            while ((CleaningRobotModel.getInstance().isWaitingForMaintenance() &&
                    CleaningRobotModel.getInstance().checkTimestamp(request.getTimestamp())) ||
                    CleaningRobotModel.getInstance().isInMaintenance()) {
                try {
                    CleaningRobotModel.getInstance().getLock().wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
            RobotServiceOuterClass.MechanicAccessResponse response = RobotServiceOuterClass.MechanicAccessResponse.newBuilder()
                    .setAck("OK from " + CleaningRobotModel.getInstance().getRobotInfo().getId())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
    }

    @Override
    public void heartbeatService(RobotServiceOuterClass.Empty request, StreamObserver<RobotServiceOuterClass.HeartbeatResponse> responseObserver) {
        RobotServiceOuterClass.HeartbeatResponse response = RobotServiceOuterClass.HeartbeatResponse.newBuilder()
                .setId(CleaningRobotModel.getInstance().getRobotInfo().getId())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}
