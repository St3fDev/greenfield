package services;

import Robot.CleaningRobotData;
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
    public Response addCleaningRobot(CleaningRobotData cleaningRobot) {
        System.out.println("ID: " + cleaningRobot.getId() + " ADDRESS: " + cleaningRobot.getAddress() + " PORT: " + cleaningRobot.getPort());
        List<CleaningRobotData> robots = GreenfieldModel.getInstance().getRobots();
        if (GreenfieldModel.getInstance().addRobot(cleaningRobot)) {
            return Response.ok(robots).entity("this robot is correctly inserted in greenfield").build();
        }
        return Response.status(Status.CONFLICT).entity("this robot already exists").build();
    }

    @Path("removeRobot/{id}")
    @DELETE
    public Response removeCleaningRobot(@PathParam("id") String id) {
        GreenfieldModel.getInstance().removeRobot(id);
        return Response.ok().entity("The robot with id: " + id + " has been removed").build();
    }
}

//TODO se un robot viene rimosso prima che un altro venga aggiunto la getRobots è inconsistente
