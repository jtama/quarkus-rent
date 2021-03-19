package com.groupeonepoint.onerent.hostels;

import com.groupeonepoint.onerent.exception.UnavailableException;
import com.groupeonepoint.onerent.exception.UnknownEntityException;
import com.groupeonepoint.onerent.reservation.Reservation;
import com.groupeonepoint.onerent.util.MonthValidator;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HostelReservationService {

    @Inject
    MonthValidator monthValidator;

    public Uni<Reservation> book(String name, int month, String userName) {
        monthValidator.validateMonth(month);

        Reservation reservation = new Reservation();
        // If id isn't null, a booking has already take place for this tuple
        return Panache.withTransaction(() ->
                Reservation.existsByUserNameAndMonthAndHouseName(userName, month, name)
                        .onItem().invoke(exists -> {
                            if (exists) {
                                throw new UnavailableException(String.format("House %s is already booked for month %s", name, month));
                            }
                            reservation.setMonth(month);
                            reservation.setUserName(userName);
                        })
                        .onItem().transformToUni(item -> Hostel.findByName(name))
                        .onItem().ifNull().failWith(() -> new UnknownEntityException(String.format("The house %s doesn't exist", name)))
                        .onItem().transformToUni(hostel -> {
                            reservation.setHouse(hostel);
                            return reservation.persist();
                        })
        ).replaceWith(reservation);
    }

}
