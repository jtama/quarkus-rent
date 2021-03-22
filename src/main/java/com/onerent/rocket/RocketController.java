package com.onerent.rocket;

import com.onerent.reservation.Reservation;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/api/rockets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RocketController {

    @Inject
    RocketReservationService rocketReservationService;

    @GET
    public List<Rocket> getAll() {
        return Rocket.listAll();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Transactional
    public Response create(@Valid Rocket rocket) {
        return Response.status(Response.Status.CREATED).entity(Rocket.persistIfNotExists(rocket)).build();

    }

    @POST
    @Path("/{name}/book")
    @RolesAllowed("USER")
    public Reservation book(@PathParam("name") String name, @QueryParam("month") Integer month, @Context SecurityContext securityContext) {
        return rocketReservationService.book(name, month, securityContext.getUserPrincipal().getName());
    }

}
