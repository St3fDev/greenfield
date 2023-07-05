package robot.GRPC;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import robot.beans.CleaningRobotModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RobotGRPCServer{

    public RobotGRPCServer(){}

    private static Server server = null;
    // TODO gestire chiusura server gRPC
    public static void startGRPCServer() throws IOException {
        //Starting the GRPC server for
            server = ServerBuilder.forPort(CleaningRobotModel.getInstance().getRobotInfo().getPort()).addService(new RobotServiceImpl()).build();
            server.start();
            System.out.println("> GRPC Server started!");

    }

    public static void stopMeGently() {
        server.shutdown();
        try {
            server.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("---------------- ROBOT GRPC SERVER CLOSED ---------------");
        server.shutdownNow();
    }
}
