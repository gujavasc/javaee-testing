package org.gujavasc.javaee.testing.bean.functional.fragment;

import org.jboss.arquillian.graphene.GrapheneElement;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;

public class Menu {

    @Root
    private GrapheneElement menu;

    private WebElement conferenceMenu;

    private WebElement talkMenu;

    private WebElement attendeeMenu;


    private WebElement getConferenceMenu() {
        if (conferenceMenu == null) {
            conferenceMenu = findItemByText("Conference");
        }
        return conferenceMenu;
    }

    private WebElement getTalkMenu() {
        if (talkMenu == null) {
            talkMenu = findItemByText("Talk");
        }
        return talkMenu;
    }

    private WebElement getAttendeeMenu() {
        if (attendeeMenu == null) {
            attendeeMenu = findItemByText("Attendee");
        }
        return attendeeMenu;
    }

    public void gotoConference() {
        guardHttp(getConferenceMenu()).click();
    }

    public void gotoTalk() {
        guardHttp(getTalkMenu()).click();
    }

    public void gotoAttendee() {
        guardHttp(getAttendeeMenu()).click();
    }

    private WebElement findItemByText(String text) {
        return menu.findElement(By.xpath("//a[text() = '" + text + "']"));
    }


}
