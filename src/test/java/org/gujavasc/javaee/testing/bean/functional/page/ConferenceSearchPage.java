package org.gujavasc.javaee.testing.bean.functional.page;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Location(value = "conference/search.xhtml")
public class ConferenceSearchPage {

    @FindBy(xpath = "//div[@id='content']//h1[contains(text(),'Conference')]")
    private WebElement h1;

    @FindBy(xpath = "//*[@id='search:conferenceBeanExampleName']")
    private WebElement inputNamesearch;

    @FindBy(xpath = "//*[@id='search']/span/span/a[1]")
    private WebElement btsearch;

    public boolean isDisplayed() {
        return h1 != null && h1.isDisplayed();
    }

    public void searchByName(String name) {
        inputNamesearch.clear();
        inputNamesearch.sendKeys(name);
        Graphene.guardHttp(btsearch).click();
    }
}
