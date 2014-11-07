package org.gujavasc.javaee.testing.service;

import org.gujavasc.javaee.testing.crud.Crud;
import org.gujavasc.javaee.testing.model.Conference;
import org.hibernate.criterion.MatchMode;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class ConferenceService {

    @Inject
    protected Crud<Conference> conferenceCrud;

    public Conference store(Conference entity) {
        Conference example = new Conference();
        example.setName(entity.getName());
        if (conferenceCrud.example(example, MatchMode.EXACT).count() > 0) {
            throw new RuntimeException("Conference already exists");
        }
        conferenceCrud.saveOrUpdate(entity);
        return entity;
    }

    public void remove(Conference entity) {
        conferenceCrud.delete(entity);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Conference find(Conference entity) {
        return conferenceCrud.example(entity).find();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Conference> list(Conference entity) {
        return conferenceCrud.example(entity).list();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Crud<Conference> crud() {
        return conferenceCrud;
    }

    public void clearConferences() {
        for (Conference conf : conferenceCrud.listAll()) {
            this.remove(conf);
        }
    }
}
