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

}
