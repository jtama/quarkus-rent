package com.onerent.rocket;


import com.onerent.exception.DuplicateEntityException;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

@Entity
public class Rocket extends PanacheEntity {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private RocketType type;

    public static Optional<Rocket> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

    public static Rocket persistIfNotExists(Rocket rocket) {
        if (find("name", rocket.name).count() > 0) {
            throw new DuplicateEntityException(String.format("Rocket named %s already exists", rocket.name));
        }
        rocket.persist();
        return rocket;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(RocketType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public RocketType getType() {
        return type;
    }
}
