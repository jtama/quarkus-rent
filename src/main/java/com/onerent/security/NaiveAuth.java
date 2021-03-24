package com.onerent.security;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class NaiveAuth implements HttpAuthenticationMechanism {

    @Inject
    Logger logger;

    private static final String ONERENT_PREFIX = "onerent";
    private static final String LOWERCASE_BASIC_PREFIX = ONERENT_PREFIX.toLowerCase(Locale.ENGLISH);
    protected static final ChallengeData CHALLENGE_DATA = new ChallengeData(
            HttpResponseStatus.UNAUTHORIZED.code(),
            HttpHeaderNames.WWW_AUTHENTICATE,
            "onerent realm=one-rent");

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context,
                                              IdentityProviderManager identityProviderManager) {

        var userName = context.request().headers().get("X-user-name");
        var userRoles = Set.of(Optional.ofNullable(context.request().getHeader("X-user-roles")).orElse("").split(","));
        HeaderAuthenticationRequest credential = new HeaderAuthenticationRequest(userName);
        credential.getRoles().addAll(userRoles);
        return identityProviderManager.authenticate(credential);
    }


    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().item(CHALLENGE_DATA);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Collections.singleton(HeaderAuthenticationRequest.class);
    }

    @Override
    public HttpCredentialTransport getCredentialTransport() {
        return new HttpCredentialTransport(HttpCredentialTransport.Type.AUTHORIZATION, "onerent");
    }
}
