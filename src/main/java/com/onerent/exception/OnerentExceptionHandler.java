package com.onerent.exception;

import io.smallrye.mutiny.CompositeException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class OnerentExceptionHandler {

    private static Map<Class<?>, Function<Throwable, Response>> mappers;
    private static Function<Throwable, Response> functionalError =  e -> Response.status(Response.Status.OK).entity(e.getMessage()).build();
    private static Function<Throwable, Response> defaultHandler = e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(e.getMessage())
            .build();

    static {
        mappers = new HashMap<>();
        mappers.put(DuplicateEntityException.class, e -> Response.status(Response.Status.CONFLICT)
                .entity(e.getMessage())
                .build());
        mappers.put(UnknownEntityException.class, e -> Response.status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build());
        mappers.put(NotAuthorizedException.class, e -> Response.status(Response.Status.UNAUTHORIZED)
                .entity("He non du con")
                .build());
        mappers.put(InvalidBookingException.class,functionalError);
        mappers.put(InvalidNameException.class,functionalError);
        mappers.put(UnavailableException.class,functionalError);

    }


    @ServerExceptionMapper
    public Response handleComposite(CompositeException e){
        return mappers.getOrDefault(e.getCause().getClass(), defaultHandler).apply(e.getCause());
    }

    @ServerExceptionMapper
    public Response handleException(InvalidNameException e){
        return functionalError.apply(e);
    }

    @ServerExceptionMapper
    public Response handleException(Exception e){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e.getMessage())
                .build();
    }
}
