package com.onerent.security;

import io.quarkus.security.identity.request.AuthenticationRequest;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class HeaderAuthenticationRequest implements AuthenticationRequest {
    private String userName;
    private Set<String> roles;

    public HeaderAuthenticationRequest(String userName) {
        this.userName = userName;
        this.roles = new HashSet<>();
    }

    public Principal getPrincipal() {
        return () -> userName;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
