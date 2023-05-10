package services;

import beans.CleaningRobotInfo;
import beans.Greenfield;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("robots")
public class RobotService {

    @Path("{id}")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addCleaningRobot(@PathParam("id") String id, CleaningRobotInfo cleaningRobot) {
        System.out.println("ID: " + cleaningRobot.getId() + " ADDRESS: " + cleaningRobot.getAddress() + " PORT: " + cleaningRobot.getPort());
        if (Greenfield.getInstance().addRobotTest(cleaningRobot)) {
            return Response.ok().entity("this robot is correctly inserted in greenfield").build();
        }
        return Response.status(Status.CONFLICT).entity("this robot already exists").build();
    }
}
