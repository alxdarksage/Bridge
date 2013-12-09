package org.sagebionetworks.bridge.webapp.integration.pages;

public class ProfilePanelPage {
	
	protected WebDriverFacade facade;
	
	public ProfilePanelPage(WebDriverFacade facade) {
		this.facade = facade;
	}

	public void clickSignOut() {
		facade.click("#signOutButton");
	}
	
	public void clickEditProfile() {
		facade.click("#editProfileAct");
	}
}
