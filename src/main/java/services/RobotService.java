package services;

import common.CleaningRobotData;
import server.beans.GreenfieldData;
import server.beans.GreenfieldModel;

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
        List<CleaningRobotData> robots = GreenfieldModel.getInstance().getRobots();
        if (GreenfieldModel.getInstance().addRobot(cleaningRobot)) {
            System.out.println("ID: " + cleaningRobot.getId() + " ADDRESS: " + cleaningRobot.getAddress() + " PORT: " + cleaningRobot.getPort());
            GreenfieldData details = new GreenfieldData(cleaningRobot.getPosition(), robots, cleaningRobot.getDistrict());
            return Response.ok(details).build();
        }
        return Response.status(Status.CONFLICT).entity("this cleaning robot already exists").build();
    }

    @Path("removeRobot/{id}")
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public Response removeCleaningRobot(@PathParam("id") String id) {
        if(GreenfieldModel.getInstance().removeRobot(id)) {
            System.out.println("CLEANING ROBOT " + id + " REMOVED");
            return Response.ok().entity("The cleaning robot [" + id + "] has been removed from server").build();
        }
        return Response.status(Status.GONE).entity("This cleaning robot ["+ id +"] was already removed").build();
    }
}

