package org.gujavasc.javaee.testing.bean.functional;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.arquillian.ArquillianCucumber;
import cucumber.runtime.arquillian.api.Features;
import org.gujavasc.javaee.testing.bean.functional.fragment.Menu;
import org.gujavasc.javaee.testing.bean.functional.page.ConferenceSearchPage;
import org.gujavasc.javaee.testing.bean.util.ScenarioEndpoint;
import org.gujavasc.javaee.testing.crud.Crud;
import org.gujavasc.javaee.testing.view.ConferenceBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(ArquillianCucumber.class)
@Features("features/conference/search.feature")
public class ConferenceAt {

    @Drone
    WebDriver browser;

    @ArquillianResource
    URL context;

    @FindBy(id = "navigation")
    Menu menu;

    @FindByJQuery("#homeLink")
    WebElement home;

    @Page
    ConferenceSearchPage conferenceSearch;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        WebArchive war = ShrinkWrap.create(WebArchive.class);
        war.addPackages(true, ConferenceBean.class.getPackage()).addPackages(true, "org.gujavasc.javaee.testing.rest")
                .addPackages(true, "org.gujavasc.javaee.testing.model")
                .addPackages(true, "org.gujavasc.javaee.testing.view")
                .addPackages(true, "org.gujavasc.javaee.testing.service").addClass(ScenarioEndpoint.class).addClass(Crud.class).addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"), "web.xml").addAsWebInfResource(new File("src/main/webapp/WEB-INF", "faces-config.xml"), "faces-config.xml")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class).importDirectory("src/main/webapp").as(GenericArchive.class), "/", Filters.include(".*\\.(xhtml|html|css|js|png|ico)$"));
        return war;

    }

    @Page
    ConferenceSearchPage conferenceSearchPage;

    @Before
    public void initScenario() {
        ClientRequest request = new ClientRequest(context + "rest/scenarios/conference/search/");
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response;
        try {
            response = request.get(String.class);
            assertEquals(response.getStatus(), 200);
        } catch (Exception e) {
            Assert.fail("could not initialize scenario");
        }
    }


    @Given("^i navigate to search conference page$")
    public void navigateToSearch() {
        Graphene.goTo(ConferenceSearchPage.class);
    }

    @When("^i search a conference with name \"([^\"]*)\"$")
    public void search(String name) {
        conferenceSearchPage.searchByName(name);
    }

    @Then("resulting list should contain conferences with \"([^\"]*)\" and total records of (\\d+)$")
    public void result(String name, int records) {
        List<WebElement> rows = browser.findElements(By.xpath("//*[@id='search:conferenceBeanPageItems']//tr//span"));
        assertEquals(records, rows.size());
        for (WebElement row : rows) {
            assertTrue(row.getText().contains(name));
        }
    }
}
