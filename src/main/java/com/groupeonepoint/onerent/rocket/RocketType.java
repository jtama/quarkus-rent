package com.groupeonepoint.onerent.rocket;

public enum RocketType {
    EXPLOSIVE("explosive"),
    FIRST_CLASS("first class"),
    LUXURY("luxury");

    private String luxury;

    RocketType(String luxury) {
        this.luxury = luxury;
    }

    public String getLuxury() {
        return luxury;
    }
}
