package com.groupeonepoint.onerent.rocket;

import com.groupeonepoint.onerent.exception.InvalidBookingException;
import com.groupeonepoint.onerent.exception.UnavailableException;
import com.groupeonepoint.onerent.exception.UnknownEntityException;
import com.groupeonepoint.onerent.reservation.Reservation;
import com.groupeonepoint.onerent.util.MonthValidator;

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
        Optional<Reservation> reservation = Reservation.findByUserNameAndMonthAndHouseIsNotNull(user, month);
        if (reservation.isEmpty()) {
            throw new InvalidBookingException(String.format("No house is booked for user %S on month %s", user, month));
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
