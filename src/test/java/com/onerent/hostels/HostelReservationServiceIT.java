package com.onerent.hostels;

import com.onerent.reservation.Reservation;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HostelReservationServiceIT {

    @Test
    @Order(1)
    void it_should_return_all_items() {
        List<Hostel> hostels =
                get("/api/hostels").then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().as(new TypeRef<>() {
                });
        assertThat(hostels).hasSize(1)
                .element(0).hasFieldOrPropertyWithValue("name", "Ritz");

    }

    @Test
    @Order(2)
    void it_should_create_one_when_user_is_granted() {
        Hostel toBeCreated = new Hostel();
        toBeCreated.setName("Test");
        Hostel result =
                given()
                        .with()
                        .header("X-user-roles", "ADMIN")
                        .header("X-user-name", "jo")
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .body(toBeCreated)
                        .post("/api/hostels")
                        .then()
                        .statusCode(HttpStatus.SC_CREATED)
                        .extract()
                        .body().as(new TypeRef<>() {
                });
        assertThat(result).hasFieldOrPropertyWithValue("name","Test")
                .extracting("id")
                .isNotNull();

    }

    @Test
    @Order(3)
    void it_should_not_create_one_when_user_is_not_granted() {
        Hostel toBeCreated = new Hostel();
        toBeCreated.setName("Test");
        given()
                .with()
                .header("X-user-roles", "USER")
                .header("X-user-name", "jo")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(toBeCreated)
                .post("/api/hostels")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @Order(4)
    void it_should_not_book_one_when_user_is_not_granted() {
        Hostel toBeCreated = new Hostel();
        toBeCreated.setName("Test");
        given()
                .with()
                .header("X-user-roles", "ADMIN")
                .header("X-user-name", "jo")
                .header("Accept", "application/json")
                .body(toBeCreated)
                .post("/api/hostels/Test/book?month=1")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @Order(5)
    void it_should_book_one_when_user_is_granted() {
        Hostel toBeCreated = new Hostel();
        toBeCreated.setName("Test");
        Reservation result = given()
                .with()
                .header("X-user-roles", "USER")
                .header("X-user-name", "jo")
                .header("Accept", "application/json")
                .body(toBeCreated)
                .post("/api/hostels/Test/book?month=1")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body().as(new TypeRef<>() {
        });
        assertThat(result).isNotNull()
                .matches(res -> res.id != null);
    }

    @Test
    @Order(6)
    void it_should_not_book_one_when_hostel_is_occupied() {
        Hostel toBeCreated = new Hostel();
        toBeCreated.setName("Test");
        String result = given()
                .with()
                .header("X-user-roles", "USER")
                .header("X-user-name", "jo")
                .header("Accept", "application/json")
                .body(toBeCreated)
                .post("/api/hostels/Test/book?month=1")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().asString();
        assertThat(result).contains("is already booked for month");
    }

}
