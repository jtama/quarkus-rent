package com.groupeonepoint.onerent.rocket;

import com.groupeonepoint.onerent.exception.InvalidBookingException;
import com.groupeonepoint.onerent.exception.UnavailableException;
import com.groupeonepoint.onerent.exception.UnknownEntityException;
import com.groupeonepoint.onerent.reservation.Reservation;
import com.groupeonepoint.onerent.util.MonthValidator;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class RocketReservationService {

    @Inject
    MonthValidator monthValidator;

    @Transactional(Transactional.TxType.MANDATORY)
    public Uni<Reservation> book(String name, int month, String user) {
        monthValidator.validateMonth(month);
        return Panache.withTransaction(() -> {
            Uni<Reservation> res = Reservation.findByUserNameAndMonthAndHouseIsNotNull(user, month)
                    .onItem().ifNull().failWith(() -> new InvalidBookingException(String.format("No house is booked for user %S on month %s", user, month)))
                    .onItem().transformToUni(item ->
                            Reservation.existsByMonthAndRocketName(month, name)
                                    .onItem()
                                    .transform(exists -> {
                                        if (exists) {
                                            throw new UnavailableException(String.format("Rocket %s has already been booked for month %s", name, month));
                                        }
                                        return item;
                                    })
                    );
            Uni<Rocket> rocketUni = Rocket.findByName(name).onItem().ifNull().failWith(() -> new UnknownEntityException(String.format("Rocket %s doesn't exists", name)));
            return Uni.combine().all().unis(res, rocketUni)
                    .asTuple()
                    .onItem().invoke(tuple -> {
                        tuple.getItem1().setRocket(tuple.getItem2());
                        tuple.getItem1().persist();
                    }).map(Tuple2::getItem1);
        });
    }
}
