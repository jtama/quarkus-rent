package com.onerent;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.Map;

public class DatabaseResource implements QuarkusTestResourceLifecycleManager {

    private static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:13.1")
            .withDatabaseName("onerent")
            .withUsername("postgres")
            .withPassword("mysecretpassword")
            .withExposedPorts(5432);

    @Override
    public Map<String, String> start() {
        DATABASE.start();
        return Collections.singletonMap("quarkus.datasource.reactive.url",
                String.format("postgresql://%s:%d/onerent", DATABASE.getHost(), DATABASE.getMappedPort(5432)));
    }

    @Override
    public void stop() {
        DATABASE.stop();
    }
}
