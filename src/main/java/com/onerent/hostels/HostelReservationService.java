package com.onerent.hostels;

import com.onerent.exception.UnavailableException;
import com.onerent.exception.UnknownEntityException;
import com.onerent.reservation.Reservation;
import com.onerent.util.MonthValidator;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.function.Consumer;

@ApplicationScoped
public class HostelReservationService {

    private MonthValidator monthValidator;
    private Logger logger;

    public HostelReservationService(MonthValidator monthValidator, Logger logger) {
        this.monthValidator = monthValidator;
        this.logger = logger;
    }

    public Uni<Reservation> book(String name, int month, String userName) {
        monthValidator.validateMonth(month);
        // If hostel exists and is available
        return Panache.withTransaction(() ->
                // Starts the stream
                Reservation.findByUserNameAndMonthAndHostelName(userName, month, name)
                        .onItem().ifNotNull().invoke(this::alreadyBooked)
                        .onItem().transformToUni(res -> Hostel.findByName(name))
                        .onItem().ifNull().failWith(() -> failIfHostelIsNotFound(name))
                        .onItem().ifNotNull().transformToUni(hostel -> bookHostel(hostel, userName, month))
        );
    }

    private void alreadyBooked(Reservation reservation) {
        logger.errorf("Hostel %s is already booked for month %s", reservation.getHostel().getName(), reservation.getMonth());
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
