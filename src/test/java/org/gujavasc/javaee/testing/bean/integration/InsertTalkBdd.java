package org.gujavasc.javaee.testing.bean.integration;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.arquillian.ArquillianCucumber;
import cucumber.runtime.arquillian.api.Features;
import org.gujavasc.javaee.testing.crud.Crud;
import org.gujavasc.javaee.testing.model.Attendee;
import org.gujavasc.javaee.testing.model.Talk;
import org.gujavasc.javaee.testing.service.AttendeeService;
import org.gujavasc.javaee.testing.service.TalkService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(ArquillianCucumber.class)
@Features("features/talk/insert-talk.feature")
@Transactional(TransactionMode.DISABLED)
public class InsertTalkBdd {

    @Inject
    TalkService talkService;

    @Inject
    AttendeeService attendeeService;

    Talk talk;

    int numTalks;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackage("org.gujavasc.javaee.testing.service")
                .addClass(Crud.class)
                .addPackages(true, Talk.class.getPackage())
                        // .addAsWebInfResource(new
                        // File("src/main/webapp/WEB-INF/web.xml"), "web.xml")
                .addAsResource("META-INF/persistence.xml",
                        "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }

    @Before
    public void setup() {
        talkService.clearTalks();
        attendeeService.clearAttendees();
    }

    @Given("^a talk with title \"([^\"]*)\" with (\\d+) slots and (\\d+) attenddes$")
    public void given(String title, int slots, int numAtendees) {
        //talkService.clearTalks();//@Before executed one time for all scenarios
        talk = new Talk();
        talk.setTitle(title);
        talk.setSlots(slots);
        talk.setAttendees(initAttendees(numAtendees));
    }

    @When("^i persist the talk$")
    public void when() {
        try {
            talkService.store(talk);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Then("^i should have (\\d+) talk$")
    public void then(int numTalks) {
        assertEquals(numTalks, talkService.crud().countAll());
    }

    private Set<Attendee> initAttendees(int numAtendees) {
        Set<Attendee> attendees = new HashSet<Attendee>();
        for (int i = 0; i < numAtendees; i++) {
            Attendee attendee = new Attendee();
            attendee.setName("attendee" + i);
            attendee.setAge(i);
            attendees.add(attendee);
        }
        return attendees;
    }

}
