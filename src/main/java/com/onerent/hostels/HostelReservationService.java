package com.onerent.hostels;

import com.onerent.exception.UnavailableException;
import com.onerent.exception.UnknownEntityException;
import com.onerent.reservation.Reservation;
import com.onerent.util.MonthValidator;

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

        if(Reservation.existsByUserNameAndMonthAndHostelName(userName, month, name)){
            throw new UnavailableException(String.format("Hostel %s is already booked for month %s", name, month));
        }

        Reservation reservation = new Reservation(userName,month);

        Optional<Hostel> hostel = Hostel.findByName(name);
        if (hostel.isEmpty()){
            throw new UnknownEntityException(String.format("The hostel %s doesn't exist", name));
        }
        reservation.setHostel(hostel.get());
        return reservation;
    }

}
