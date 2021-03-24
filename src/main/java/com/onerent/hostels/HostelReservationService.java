package com.onerent.hostels;

import com.onerent.exception.UnavailableException;
import com.onerent.exception.UnknownEntityException;
import com.onerent.reservation.Reservation;
import com.onerent.util.MonthValidator;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

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
                Reservation.existsByUserNameAndMonthAndHostelName(userName, month, name)
                        .onItem()
                        // Chains to another stream
                        .transformToUni(exists -> {
                            if (exists) {
                                throw new UnavailableException(String.format("Hostel %s is already booked for month %s", name, month));
                            }
                            return Hostel.findByName(name);
                            })
                        .onItem()
                        // Chains to the last one
                        .transformToUni(hostel -> {
                            if (hostel == null) {
                                throw new UnknownEntityException(String.format("The hostel %s doesn't exist", name));
                            }
                            Reservation reservation = new Reservation();
                            reservation.setMonth(month);
                            reservation.setUserName(userName);
                            reservation.setHostel(hostel);
                            return reservation.persist().replaceWith(reservation);
                        })
        );
    }

}
