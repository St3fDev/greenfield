package services;

import beans.GreenfieldModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("adminClient")
public class ClientService {

    @Path("getRobots")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAllRobots() {
        return Response.ok(GreenfieldModel.getInstance()).build();
    }

    @Path("last_n_avg_pollution/{robot_id}/{value}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLastNAveragePollution(@PathParam("robot_id") String id, @PathParam("value") String value) {
        return Response.ok().build();
    }

    @Path("average_pollution_level/{t1}/{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAveragePollutionLevel(@PathParam("t1") String t1, @PathParam("t2") String t2) {
        return Response.ok().build();
    }
}
