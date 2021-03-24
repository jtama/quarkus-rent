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
            Uni<Reservation> res = Reservation.findByUserNameAndMonthAndHostelIsNotNull(user, month)
                    .onItem().transformToUni(item -> {
                        if (item == null) {
                            throw new InvalidBookingException(String.format("No hostel is booked for user %S on month %s", user, month));
                        }
                        return Reservation.existsByMonthAndRocketName(month, name)
                                .map(exists -> {
                                    if (exists) {
                                        throw new UnavailableException(String.format("Rocket %s has already been booked for month %s", name, month));
                                    }
                                    return item; // Not happy with this closure use.
                                });
                    });
            Uni<Rocket> rocketUni = Rocket.findByName(name)
                    .onItem()
                    .ifNull().failWith(() -> new UnknownEntityException(String.format("Rocket %s doesn't exists", name)));
            // Combine triggers incomming Unis.
            return Uni.combine().all().unis(res, rocketUni)
                    .asTuple()
                    .onItem().invoke(tuple -> {
                        tuple.getItem1().setRocket(tuple.getItem2());
                        tuple.getItem1().persist();
                    }).map(Tuple2::getItem1);
        });
    }
}
