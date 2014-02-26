package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class PortalPage extends EmbeddedSignInPage {

	public static final String TITLE = "Patients & Researchers in Partnership";
	public static final String URL = "/portal/index.html";
	
	public PortalPage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void assertAdminLinkMissing() {
		List<WebElement> elements = facade.findElements(By.cssSelector("#adminAct"));
		Assert.assertEquals(0, elements.size());
	}
	
	public void clickAdmin() {
		facade.click("#adminAct");
	}
	public void clickSignOut() {
		facade.click("#signOutAct");
	}
	
}
