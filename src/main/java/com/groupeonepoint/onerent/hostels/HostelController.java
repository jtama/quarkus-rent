package com.groupeonepoint.onerent.hostels;

import com.groupeonepoint.onerent.reservation.Reservation;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/api/hostels")
@Produces(MediaType.APPLICATION_JSON)
public class HostelController {

    @Inject
    HostelReservationService hostelReservationService;

    @GET
    public List<Hostel> getAll() {
        return Hostel.listAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    @Transactional
    public Hostel create(Hostel hostel) {
        return Hostel.persistIfNotExists(hostel);
    }

    @POST
    @Path("/{name}/book")
    @RolesAllowed("USER")
    public Reservation book(@PathParam("name")String name, @QueryParam("month") Integer month, @Context SecurityContext security) {
        return hostelReservationService.book(name, month, security.getUserPrincipal().getName());
    }

}

