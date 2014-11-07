package org.gujavasc.javaee.testing.bean.util;

import org.gujavasc.javaee.testing.model.Conference;
import org.gujavasc.javaee.testing.model.Talk;
import org.gujavasc.javaee.testing.service.ConferenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Stateless
@Path("/scenarios")
public class ScenarioEndpoint {

    @Inject
    ConferenceService conferenceService;


    @GET
    @Path("/conference/search")
    public Response initConferenceSearchScenario() {
        this.clear();
        Conference tdcPoa = new Conference();
        tdcPoa.setName("Serra StarTec 2014");
        Talk talk = new Talk();
        talk.setTitle("Unit Testing com Java EE");
        talk.setSlots(10);
        Conference javaOne = new Conference();
        javaOne.setName("JavaOne");
        Conference jaxLondon = new Conference();
        jaxLondon.setName("jaxLondon");
        Conference tdcSaoPaulo = new Conference();
        tdcSaoPaulo.setName("TDC São Paulo");
        conferenceService.store(tdcPoa);
        conferenceService.store(tdcSaoPaulo);
        conferenceService.store(jaxLondon);
        conferenceService.store(javaOne);
        return Response.ok().build();
    }

    @GET
    @Path("/clear")
    public Response clear() {
        conferenceService.clearConferences();
        return Response.ok().build();
    }

}
