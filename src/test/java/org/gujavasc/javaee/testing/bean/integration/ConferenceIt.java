package org.gujavasc.javaee.testing.bean.integration;

import org.gujavasc.javaee.testing.crud.Crud;
import org.gujavasc.javaee.testing.model.Conference;
import org.gujavasc.javaee.testing.rest.ConferenceEndpoint;
import org.gujavasc.javaee.testing.service.ConferenceService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.*;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ConferenceIt {

    @Inject
    private ConferenceService conferenceService;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addClass(ConferenceService.class)
                .addClass(Crud.class)
                .addPackages(true, Conference.class.getPackage())
                .addPackages(true, ConferenceEndpoint.class.getPackage())
                        //.addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"), "web.xml")
                .addAsResource("META-INF/persistence.xml",
                        "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }

    @Test
    @InSequence(1)
    public void shouldInsertConference() {
        Conference conference = new Conference();
        conference.setName("Serra StarTec 2014");
        Conference conferenceInserted = conferenceService.store(conference);
        assertNotNull(conferenceInserted);
        assertNotNull(conferenceInserted.getId());
    }

    @Test
    @InSequence(2)
    public void shouldRemoveConference() {
        int numConveferences = conferenceService.crud().countAll();
        Conference conferenceSearch = new Conference();
        conferenceSearch.setName("Serra StarTec 2014");
        Conference conferenceToRemove = conferenceService.find(conferenceSearch);
        assertNotNull(conferenceToRemove);
        conferenceService.remove(conferenceToRemove);
        assertEquals(numConveferences - 1, conferenceService.crud().countAll());
    }

    @Test
    @UsingDataSet("conference.yml")
    @Cleanup(phase = TestExecutionPhase.BEFORE)
    @InSequence(3)
    public void shouldFailToInsertConferenceWithDuplicateName() {
        Conference conference = new Conference();
        conference.setName("Serra StarTec 2014");
        try {
            conferenceService.store(conference);
        } catch (RuntimeException e) {
            assertEquals("Conference already exists", e.getCause().getMessage());
        }
        assertNull(conference.getId());
    }

    @Test
    @CleanupUsingScript(value = "clean.sql", phase = TestExecutionPhase.BEFORE)
    @ShouldMatchDataSet(value = "conference_created.yml", excludeColumns = "id")
    @InSequence(4)
    public void shouldInsertConferenceWithSuccess() {
        Conference conference = new Conference();
        conference.setName("Serra StarTec 2014");
        conferenceService.store(conference);
        //assertNotNull(conference.getId()); @ShouldMatchDataset
    }

    @Test
    @CleanupUsingScript(value = "clean.sql", phase = TestExecutionPhase.BEFORE)
    @UsingDataSet("conference.yml")
    @ShouldMatchDataSet("conference_empty.yml")
    @InSequence(5)
    public void shouldRemoveConferencesWithSuccess() {
        List<Conference> conferences = conferenceService.crud().listAll();
        assertEquals(2, conferences.size());
        for (Conference conference : conferences) {
            conferenceService.remove(conference);
        }
        //assertEquals(0, conferences.size()); @ShouldMatchDataset
    }

    @RunAsClient
    @Test
    public void shouldCallEndPoint(@ArquillianResource URL url) {
        ClientRequest request = new ClientRequest(url + "rest/conferences/test/");
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response;
        try {
            response = request.get(String.class);
            assertEquals(response.getStatus(), 200);
            assertEquals("hello from rest", response.getEntity());
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
}
