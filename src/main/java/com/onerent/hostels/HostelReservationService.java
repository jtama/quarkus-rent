package com.onerent.hostels;

import com.onerent.exception.UnavailableException;
import com.onerent.exception.UnknownEntityException;
import com.onerent.reservation.Reservation;
import com.onerent.util.MonthValidator;
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


        // If id isn't null, a booking has already take place for this tuple
        return Panache.withTransaction(() ->
                Reservation.existsByUserNameAndMonthAndHostelName(userName, month, name)
                        .onItem().invoke(exists -> {
                            if (exists) {
                                throw new UnavailableException(String.format("Hostel %s is already booked for month %s", name, month));
                            }
                        })
                        .onItem().transformToUni(item -> Hostel.findByName(name)) // transformToUni to trigger subscription I think
                        .onItem().ifNull().failWith(() -> new UnknownEntityException(String.format("The hostel %s doesn't exist", name)))
                        .onItem().transformToUni(hostel -> {
                            Reservation reservation = new Reservation();
                            reservation.setMonth(month);
                            reservation.setUserName(userName);
                            reservation.setHostel(hostel);
                            return reservation.persist().replaceWith(reservation);
                        })
        );
    }

}
