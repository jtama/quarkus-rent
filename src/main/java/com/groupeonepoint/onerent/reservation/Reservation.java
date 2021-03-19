package com.groupeonepoint.onerent.reservation;

import com.groupeonepoint.onerent.hostels.Hostel;
import com.groupeonepoint.onerent.rocket.Rocket;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Optional;

@Entity
public class Reservation extends PanacheEntity {

    private String userName;

    private int month;

    @OneToOne
    @JoinColumn(name = "hostel_id")
    private Hostel house;

    @OneToOne
    @JoinColumn(name = "rocket_id")
    private Rocket rocket;

    public static Boolean existsByUserNameAndMonthAndHouseName(String user, int month, String name) {
        return count("userName = ?1 and month = ?2 and house.name = ?3", user, month, name) > 0;
    }

    public static Optional<Reservation> findByUserNameAndMonthAndHouseIsNotNull(String user, int month) {
        return find("userName = ?1 and month = ?2 and house is not null", user, month).firstResultOptional();
    }

    public static Boolean existsByMonthAndRocketName(int month, String name) {
        return count("month = ?1 and rocket.name = ?2", month, name) > 0;
    }

    public Reservation() {
    }

    public Reservation(String userName, int month) {
        this.userName = userName;
        this.month = month;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Hostel getHouse() {
        return house;
    }

    public void setHouse(Hostel house) {
        this.house = house;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }
}
