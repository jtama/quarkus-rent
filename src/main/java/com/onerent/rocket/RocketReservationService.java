package com.onerent.rocket;

import com.onerent.exception.InvalidBookingException;
import com.onerent.exception.UnavailableException;
import com.onerent.exception.UnknownEntityException;
import com.onerent.reservation.Reservation;
import com.onerent.util.MonthValidator;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class RocketReservationService {

    private MonthValidator monthValidator;

    public RocketReservationService(MonthValidator monthValidator) {
        this.monthValidator = monthValidator;
    }

    public Uni<Reservation> book(String name, int month, String user) {
        monthValidator.validateMonth(month);
        return Panache.withTransaction(() -> {
            Uni<Reservation> hostelRes =
                    Reservation.findByUserNameAndMonthAndHostelIsNotNull(user, month)
                        .onItem().ifNull().failWith(noHostelBooked(user, month));
            Uni<Boolean> rocketReserved =
                    Reservation.existsByMonthAndRocketName(month, name)
                        .onItem().invoke(booked -> failedIfBooked(booked, name, month));
            Uni<Rocket> rocket =
                    Rocket.findByName(name)
                        .onItem().ifNull().failWith(() -> new UnknownEntityException(String.format("Rocket %s doesn't exists", name)));
            // Combine triggers incomming Unis.
            return Uni.combine().all().unis(hostelRes, rocket, rocketReserved)
                    .combinedWith(this::bookRocket)
                    .onItem().invoke(reservation -> reservation.persist().replaceWith(reservation));
        });
    }

    private void failedIfBooked(boolean booked, String name, int month){
        if (booked) {
            throw new UnavailableException(String.format("Rocket %s has already been booked for month %s", name, month));
        }
    }

    private InvalidBookingException noHostelBooked(String user, int month) {
        return new InvalidBookingException(String.format("No hostel is booked for user %S on month %s", user, month));
    }

    private Reservation bookRocket(Reservation res, Rocket rocket, Boolean ignored){
        res.setRocket(rocket);
        return res;
    }
}
