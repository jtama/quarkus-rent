package com.onerent.rocket;

import com.onerent.exception.InvalidBookingException;
import com.onerent.exception.UnavailableException;
import com.onerent.exception.UnknownEntityException;
import com.onerent.reservation.Reservation;
import com.onerent.util.MonthValidator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class RocketReservationService {

    @Inject
    MonthValidator monthValidator;

    @Transactional
    public Reservation book(String name, int month, String user) {
        monthValidator.validateMonth(month);
        Optional<Reservation> reservation = Reservation.findByUserNameAndMonthAndHostelIsNotNull(user, month);
        if (reservation.isEmpty()) {
            throw new InvalidBookingException(String.format("No hostel is booked for user %S on month %s", user, month));
        }
        Boolean booked = Reservation.existsByMonthAndRocketName(month, name);
        if (booked) {
            throw new UnavailableException(String.format("Rocket %s has already been booked for month %s", name, month));
        }
        Optional<Rocket> rocket = Rocket.findByName(name);
        if (rocket.isEmpty()) {
            throw new UnknownEntityException(String.format("Rocket %s doesn't exists", name));
        }
        reservation.get().setRocket(rocket.get());
        return reservation.get();
    }
}
