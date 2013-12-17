package org.sagebionetworks.bridge.webapp.integration.pages;

public class ProfilePanelPage {
	
	protected WebDriverFacade facade;
	
	public ProfilePanelPage(WebDriverFacade facade) {
		this.facade = facade;
	}

	public void clickSignOut() {
		facade.executeJavaScript("document.getElementsByClassName('smenu')[0].style.display='block';");
		facade.click("#signOutButton");
	}
	
	public void clickEditProfile() {
		facade.executeJavaScript("document.getElementsByClassName('smenu')[0].style.display='block';");
		facade.click("#editProfileAct");
	}
}
