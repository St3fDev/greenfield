package services;

import beans.CleaningRobotData;
import beans.GreenfieldDetails;
import beans.GreenfieldModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("robots")
public class RobotService {

    @Path("addRobot")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addCleaningRobot(CleaningRobotData cleaningRobot) {
        System.out.println("ID: " + cleaningRobot.getId() + " ADDRESS: " + cleaningRobot.getAddress() + " PORT: " + cleaningRobot.getPort());
        List<CleaningRobotData> robots = GreenfieldModel.getInstance().getRobots();
        if (GreenfieldModel.getInstance().addRobot(cleaningRobot)) {
            GreenfieldDetails details = new GreenfieldDetails(cleaningRobot.getPosition(), robots, cleaningRobot.getDistrict());
            return Response.ok(details).build();
        }
        return Response.status(Status.CONFLICT).entity("this robot already exists").build();
    }

    @Path("removeRobot/{id}")
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public Response removeCleaningRobot(@PathParam("id") String id) {
        GreenfieldModel.getInstance().removeRobot(id);
        System.out.println("ROBOT : " + id + " REMOVED");
        return Response.ok().entity("The robot with id: " + id + " has been removed").build();
    }
}

