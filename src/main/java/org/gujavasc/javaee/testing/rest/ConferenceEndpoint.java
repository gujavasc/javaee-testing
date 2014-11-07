package org.gujavasc.javaee.testing.rest;

import org.gujavasc.javaee.testing.model.Conference;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

@Stateless
@Path("/conferences")
public class ConferenceEndpoint {
    @PersistenceContext(unitName = "forge-default")
    private EntityManager em;


    @GET
    @Path("/test")
    @Produces("application/json")
    public Response teste() {
        return Response.ok("hello from rest").build();
    }

    @POST
    @Consumes("application/json")
    public Response create(Conference entity) {
        em.persist(entity);
        return Response.created(UriBuilder.fromResource(ConferenceEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        Conference entity = em.find(Conference.class, id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        em.remove(entity);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json")
    public Response findById(@PathParam("id") Long id) {
        TypedQuery<Conference> findByIdQuery = em.createQuery("SELECT DISTINCT c FROM Conference c LEFT JOIN FETCH c.Talks WHERE c.id = :entityId ORDER BY c.id", Conference.class);
        findByIdQuery.setParameter("entityId", id);
        Conference entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @GET
    @Produces("application/json")
    public List<Conference> listAll() {
        final List<Conference> results = em.createQuery("SELECT DISTINCT c FROM Conference c LEFT JOIN FETCH c.Talks ORDER BY c.id", Conference.class).getResultList();
        return results;
    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(Conference entity) {
        entity = em.merge(entity);
        return Response.noContent().build();
    }
}