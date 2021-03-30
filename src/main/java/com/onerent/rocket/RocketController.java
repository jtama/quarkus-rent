package com.onerent.rocket;

import com.onerent.reservation.Reservation;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/api/rockets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RocketController {

    private RocketReservationService rocketReservationService;
    private Logger logger;

    public RocketController(RocketReservationService rocketReservationService, Logger logger) {
        this.rocketReservationService = rocketReservationService;
        this.logger = logger;
    }

    @GET
    public Multi<Rocket> getAll() {
        return Rocket.streamAll();
    }

    @POST
    @RolesAllowed("ADMIN")
    public Uni<Response> create(@Valid Rocket rocket) {
        return Rocket.persistIfNotExists(rocket)
                .map(item -> Response.status(Response.Status.CREATED).entity(item).build());

    }

    @POST
    @Path("/{name}/book")
    @RolesAllowed("USER")
    public Uni<Reservation> book(String name, @RestQuery Integer month, SecurityContext securityContext) {
        logger.infof("Received book request : {} {}", name, month);
        return rocketReservationService.book(name, month, securityContext.getUserPrincipal().getName());
    }

}
