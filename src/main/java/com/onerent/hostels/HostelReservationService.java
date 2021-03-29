package com.onerent.hostels;

import com.onerent.exception.UnavailableException;
import com.onerent.exception.UnknownEntityException;
import com.onerent.reservation.Reservation;
import com.onerent.util.MonthValidator;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HostelReservationService {

    private MonthValidator monthValidator;

    public HostelReservationService(MonthValidator monthValidator) {
        this.monthValidator = monthValidator;
    }

    public Uni<Reservation> book(String name, int month, String userName) {
        monthValidator.validateMonth(month);
        // If hostel exists and is available
        return Panache.withTransaction(() ->
                // Starts the stream
                Reservation.findByUserNameAndMonthAndHostelName(userName, month, name)
                        .onItem().ifNotNull().invoke(this::alreadyBooked)
                        // Chains to another stream
                        .onItem().transformToUni(reservation -> Hostel.findByName(name))
                        .onItem().ifNull().failWith(failIfHostelIsNotFound(name))
                        // Chains to the last one
                        .onItem().transformToUni(hostel -> bookHostel(hostel, userName, month))
        );
    }

    private void alreadyBooked(Reservation reservation) {
        throw new UnavailableException(String.format("Hostel %s is already booked for month %s", reservation.getHostel().getName(), reservation.getMonth()));
    }

    private UnknownEntityException failIfHostelIsNotFound(String name) {
        return new UnknownEntityException(String.format("Hostel %s doesn't exist", name));
    }

    private Uni<Reservation> bookHostel(Hostel hostel, String userName, int month) {
        Reservation reservation = new Reservation();
        reservation.setMonth(month);
        reservation.setUserName(userName);
        reservation.setHostel(hostel);
        return reservation.persist().replaceWith(reservation);
    }

}
