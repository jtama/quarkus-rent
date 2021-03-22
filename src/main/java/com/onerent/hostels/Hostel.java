package com.onerent.hostels;

import com.onerent.exception.DuplicateEntityException;
import com.onerent.exception.InvalidNameException;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class Hostel extends PanacheEntity {

    private static final String PATTERN = "[a-zA-Z]([-a-z0-9]*[a-z0-9])";
    private static final Pattern NAME_PATTERN = Pattern.compile(PATTERN);

    private String name;

    public static Hostel persistIfNotExists(Hostel hostel) {
        if (!validateName(hostel.getName())) {
            throw new InvalidNameException(String.format("Name %s is invalid for a hostel. Name should comply the following pattern %s", hostel.getName(), PATTERN));
        }
        if (Hostel.count("name", hostel.name) > 0) {
            throw new DuplicateEntityException(String.format("Hostel %s already exists", hostel.name));
        }
        hostel.persist();
        return hostel;
    }

    private static boolean validateName(String name) {
        Matcher matcher = NAME_PATTERN.matcher(name);
        return matcher.matches();
    }

    public static Optional<Hostel> findByName(String name) {
        return find("name", name).firstResultOptional();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
