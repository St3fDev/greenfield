package services;

import Robot.CleaningRobotData;
import beans.GreenfieldModel;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("adminClient")
public class ClientService {

    @Path("getRobots")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAllRobots() {
        String robots = new Gson().toJson(GreenfieldModel.getInstance().getRobots());
        return Response.ok(robots).build();
    }

    @Path("last_n_avg_pollution/{robot_id}/{value}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLastNAveragePollution(@PathParam("robot_id") String id, @PathParam("value") String value) {
        double average = GreenfieldModel.getInstance().avgLastNAirPollutionLevel(id, Integer.parseInt(value));
        if (average != 0.0) {
            String avg = new Gson().toJson(average);
            return Response.ok(avg).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).entity("there is no robot with id: " + id + " in Greenfield").build();
        }
    }

    @Path("average_pollution_level/{t1}/{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAveragePollutionLevel(@PathParam("t1") String t1, @PathParam("t2") String t2) {
        double average = GreenfieldModel.getInstance().averageAirPollutionLevelInRange(Long.parseLong(t1), Long.parseLong(t2));
        if (average != 0.0) {
            return Response.ok(average).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).entity("No statistics were made, come back later").build();
        }
    }
}
