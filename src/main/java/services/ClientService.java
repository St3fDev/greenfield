package services;

import com.google.gson.Gson;
import common.RobotListResponse;
import server.beans.GreenfieldModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("adminClient")
public class ClientService {

    @Path("getRobots")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAllRobots() {
        if (!GreenfieldModel.getInstance().getRobots().isEmpty()) {
            String robots = new Gson().toJson(new RobotListResponse(GreenfieldModel.getInstance().getRobots()));
            return Response.ok(robots).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("There are no robot in Greenfield, please come back later.")
                .build();
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
            return Response.status(Response.Status.NOT_FOUND).entity("This cleaning robot [" + id + "] is not in greenfield").build();
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
