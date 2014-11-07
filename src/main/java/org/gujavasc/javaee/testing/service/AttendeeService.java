package org.gujavasc.javaee.testing.service;

import org.gujavasc.javaee.testing.crud.Crud;
import org.gujavasc.javaee.testing.model.Attendee;
import org.hibernate.criterion.MatchMode;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class AttendeeService {

    @Inject
    protected Crud<Attendee> attendeeCrud;

    public Attendee store(Attendee entity) {
        Attendee example = new Attendee();
        example.setName(entity.getName());
        if (attendeeCrud.example(example, MatchMode.EXACT).count() > 0) {
            throw new RuntimeException("Attendee already exists");
        }
        attendeeCrud.saveOrUpdate(entity);
        return entity;
    }

    public void remove(Attendee entity) {
        attendeeCrud.delete(entity);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Attendee find(Attendee entity) {
        return attendeeCrud.example(entity).find();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Attendee> list(Attendee entity) {
        return attendeeCrud.example(entity).list();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Crud<Attendee> crud() {
        return attendeeCrud;
    }

    public void clearAttendees() {
        for (Attendee Attendee : attendeeCrud.listAll()) {
            this.remove(Attendee);
        }
    }
}
