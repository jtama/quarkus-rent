package com.groupeonepoint.onerent.hostels;

import com.groupeonepoint.onerent.exception.UnavailableException;
import com.groupeonepoint.onerent.exception.UnknownEntityException;
import com.groupeonepoint.onerent.reservation.Reservation;
import com.groupeonepoint.onerent.util.MonthValidator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class HostelReservationService {

    @Inject
    MonthValidator monthValidator;

    @Transactional
    public Reservation book(String name, int month, String userName) {
        monthValidator.validateMonth(month);

        if(Reservation.existsByUserNameAndMonthAndHouseName(userName, month, name)){
            throw new UnavailableException(String.format("House %s is already booked for month %s", name, month));
        }

        Reservation reservation = new Reservation(userName,month);

        Optional<Hostel> hostel = Hostel.findByName(name);
        if (hostel.isEmpty()){
            throw new UnknownEntityException(String.format("The house %s doesn't exist", name));
        }
        reservation.setHouse(hostel.get());
        return reservation;
    }

}
