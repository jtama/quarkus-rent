package com.onerent.hostels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onerent.exception.DuplicateEntityException;
import com.onerent.exception.InvalidNameException;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;

import javax.persistence.Entity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public class Hostel extends PanacheEntity {

    private static final String PATTERN = "[a-zA-Z]([-a-z0-9]*[a-z0-9])";
    private static final Pattern NAME_PATTERN = Pattern.compile(PATTERN);

    private String name;

    public static Uni<Hostel> persistIfNotExists(Hostel hostel) {
        if (!validateName(hostel.getName())) {
            throw new InvalidNameException(String.format("Name %s is invalid for a hostel. Name should comply the following pattern %s", hostel.getName(), PATTERN));
        }
        return Panache.withTransaction(() ->
                Hostel.count("name", hostel.name)
                        .map(item -> item > 0)
                        .onItem()
                        .transformToUni(exists -> {
                            if (exists) {
                                throw new DuplicateEntityException(String.format("Hostel %s already exists", hostel.name));
                            }
                            return hostel.persist();
                        }))
                .replaceWith(hostel);
    }

    private static boolean validateName(String name) {
        Matcher matcher = NAME_PATTERN.matcher(name);
        return matcher.matches();
    }

    public static Uni<Hostel> findByName(String name) {
        return find("name", name).firstResult();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
