package GRPC;

import Robot.CleaningRobotDetails;
import beans.CleaningRobotData;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class RobotGRPCServer extends Thread{

    public RobotGRPCServer(){}

    @Override
    public void run() {
        //Starting the GRPC server for
        try {
            Server server = ServerBuilder.forPort(CleaningRobotDetails.getInstance().getRobotInfo().getPort()).addService(new RobotServiceImpl()).build();
            server.start();
            System.out.println("> GRPC Server started!");
            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
