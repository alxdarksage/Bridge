package org.sagebionetworks.bridge.webapp.integration.pages;


public class PortalPage {

	private WebDriverFacade facade;
	
	public PortalPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void clickAdmin() {
		facade.click("#adminAct");
	}
	public void clickSignOut() {
		facade.click("#signOutAct");
	}
	
}
