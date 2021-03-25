package com.onerent.hostels;

import com.onerent.rocket.Rocket;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
//@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HostelReservationServiceIT {

    @Test
    @Order(1)
    void testInitialItems() {
        List<Hostel> hostels = get("/api/hostels").then()
                .statusCode(HttpStatus.SC_OK)
                .extract().body().as(new TypeRef<>() {

                });

        List<Rocket> rockets = get("/api/rockets").then()
                .statusCode(HttpStatus.SC_OK)
                .header(HttpHeaders.CONTENT_TYPE, startsWith(MediaType.APPLICATION_JSON))
                .extract().body().as(new TypeRef<>() {
                });

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(hostels).hasSize(1)
                    .element(0).hasFieldOrPropertyWithValue("name", "Ritz");
            softly.assertThat(rockets).hasSize(1)
                    .element(0).hasFieldOrPropertyWithValue("name", "APOLLO 1");
        });

    }

}
