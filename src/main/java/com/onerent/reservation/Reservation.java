package com.onerent.reservation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onerent.hostels.Hostel;
import com.onerent.rocket.Rocket;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reservation extends PanacheEntity {

    private String userName;

    private int month;

    @OneToOne
    @JoinColumn(name = "hostel_id")
    private Hostel hostel;

    @OneToOne
    @JoinColumn(name = "rocket_id")
    private Rocket rocket;

    public static Uni<Reservation> findByUserNameAndMonthAndHostelIsNotNull(String user, int month) {
        return find("userName = ?1 and month = ?2 and hostel is not null", user, month).firstResult();
    }

    public static Uni<Boolean> existsByMonthAndRocketName(int month, String name) {
        return count("month = ?1 and rocket.name = ?2", month, name)
                .map(item -> item > 0);
    }

    public static Uni<Reservation> findByUserNameAndMonthAndHostelName(String userName, int month, String name) {
        return find("userName = ?1 and month = ?2 and hostel.name = ?3", userName, month, name).firstResult();
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

    public Hostel getHostel() {
        return hostel;
    }

    public void setHostel(Hostel hostel) {
        this.hostel = hostel;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }
}
