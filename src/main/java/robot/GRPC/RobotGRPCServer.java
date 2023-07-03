package robot.GRPC;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import robot.beans.CleaningRobotDetails;

import java.io.IOException;

public class RobotGRPCServer{

    public RobotGRPCServer(){}

    // TODO gestire chiusura server gRPC
    public static void startGRPCServer() throws IOException {
        //Starting the GRPC server for
            Server server = ServerBuilder.forPort(CleaningRobotDetails.getInstance().getRobotInfo().getPort()).addService(new RobotServiceImpl()).build();
            server.start();
            System.out.println("> GRPC Server started!");
    }
}
