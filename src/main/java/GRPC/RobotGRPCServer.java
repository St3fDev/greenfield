package GRPC;

import Robot.CleaningRobotData;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class RobotGRPCServer extends Thread{

    private CleaningRobotData robot;

    public RobotGRPCServer(CleaningRobotData robot){
        this.robot = robot;
    }

    @Override
    public void run() {
        //Starting the GRPC server for
        try {
            Server server = ServerBuilder.forPort(robot.getPort()).addService(new RobotServiceImpl(robot)).build();
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
