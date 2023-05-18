package services;

import beans.CleaningRobotData;
import beans.Greenfield;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("robots")
public class RobotService {

    @Path("addRobot")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addCleaningRobot(CleaningRobotData cleaningRobot) {
        System.out.println("ID: " + cleaningRobot.getId() + " ADDRESS: " + cleaningRobot.getAddress() + " PORT: " + cleaningRobot.getPort());
        List<CleaningRobotData> robots = Greenfield.getInstance().getRobots();
        if (Greenfield.getInstance().addRobotTest(cleaningRobot)) {
            return Response.ok(robots).entity("this robot is correctly inserted in greenfield").build();
        }
        return Response.status(Status.CONFLICT).entity("this robot already exists").build();
    }
}
