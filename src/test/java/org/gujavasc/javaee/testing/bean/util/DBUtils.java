package org.gujavasc.javaee.testing.bean.util;

import org.gujavasc.javaee.testing.model.Attendee;
import org.gujavasc.javaee.testing.model.Conference;
import org.gujavasc.javaee.testing.model.Talk;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;


@Singleton
@Startup
public class DBUtils {

    @PersistenceContext
    EntityManager entityManager;

    @PostConstruct
    public void intBD() {
        createDataset();
    }

    private void createDataset() {
        Conference conference = new Conference();
        conference.setName("Serra StarTec 2014");
        Talk talk = new Talk();
        talk.setTitle("Unit Testing com Java EE");
        talk.setSlots(10);
        talk.setAttendees(initAttendees());
        Set<Talk> talks = new HashSet<Talk>();
        talks.add(talk);
        conference.setTalks(talks);
        entityManager.persist(conference);
        Conference javaOne = new Conference();
        javaOne.setName("JavaOne");
        Conference jaxLondon = new Conference();
        jaxLondon.setName("jaxLondon");
        Conference tdcSaoPaulo = new Conference();
        tdcSaoPaulo.setName("TDC São Paulo");
        entityManager.persist(tdcSaoPaulo);
        entityManager.persist(javaOne);
        entityManager.persist(jaxLondon);
        entityManager.flush();
    }

    private Set<Attendee> initAttendees() {
        Set<Attendee> attendees = new HashSet<Attendee>();
        for (int i = 0; i < 5; i++) {
            Attendee attendee = new Attendee();
            attendee.setName("attendee" + i);
            attendee.setAge(i);
            attendees.add(attendee);
        }
        return attendees;
    }

}
