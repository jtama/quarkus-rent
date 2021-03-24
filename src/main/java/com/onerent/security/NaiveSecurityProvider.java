package com.onerent.security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class NaiveSecurityProvider implements IdentityProvider<HeaderAuthenticationRequest> {

    @Inject
    Logger logger;

    @Override
    public Class<HeaderAuthenticationRequest> getRequestType() {
        return HeaderAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(HeaderAuthenticationRequest request, AuthenticationRequestContext context) {
        return Uni.createFrom().item(() -> {
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
            builder.setPrincipal(request.getPrincipal());
            request.getRoles().forEach(builder::addRole);
            return builder.build();
        });
    }

}
