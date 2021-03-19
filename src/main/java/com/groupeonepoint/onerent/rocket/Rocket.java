package com.groupeonepoint.onerent.rocket;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.groupeonepoint.onerent.exception.DuplicateEntityException;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Rocket extends PanacheEntity {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private RocketType type;

    public static Uni<Rocket> findByName(String name) {
        return find("name", name).firstResult();
    }

    public static Uni<Rocket> persistIfNotExists(Rocket rocket) {
        return Panache.withTransaction(
                () -> find("name", rocket.name)
                        .count()
                        .onItem()
                        .invoke(count -> {
                                    if (count > 0) {
                                        throw new DuplicateEntityException(String.format("Rocket named %s already exists", rocket.name));
                                    }
                                }
                        )
                        .chain(count -> rocket.persist()))
                .replaceWith(rocket);
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
