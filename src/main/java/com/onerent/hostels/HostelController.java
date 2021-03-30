package com.onerent.hostels;

import com.onerent.reservation.Reservation;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/api/hostels")
@Produces(MediaType.APPLICATION_JSON)
public class HostelController {

    private HostelReservationService hostelReservationService;
    private Logger logger;

    public HostelController(HostelReservationService hostelReservationService, Logger logger) {
        this.hostelReservationService = hostelReservationService;
        this.logger = logger;
    }

    @GET
    public Multi<Hostel> getAll() {
        return Hostel.streamAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Uni<Hostel> create(Hostel hostel) {
        return Hostel.persistIfNotExists(hostel);
    }

    @POST
    @Path("/{name}/book")
    @RolesAllowed("USER")
    public Uni<Reservation> book(String name, @RestQuery("month") Integer month, SecurityContext security) {
        logger.infof("Received book request : %s %s", name, month);
        return hostelReservationService.book(name, month, security.getUserPrincipal().getName());
    }

}

