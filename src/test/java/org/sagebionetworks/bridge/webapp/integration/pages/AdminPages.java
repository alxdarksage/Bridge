package org.sagebionetworks.bridge.webapp.integration.pages;

public abstract class AdminPages extends EmbeddedSignInPage {

	public static final String TITLE = "Admin Dashboard";
	
	public AdminPages(WebDriverFacade facade) {
		super(facade);
	}
	
	public void clickAdminHome() {
		facade.click("#adminAct");
	}

	public void clickCommunities() {
		facade.click("#communitiesAct");
	}
	
	public void clickUsers() {
		facade.click("#usersAct");
	}
	
}
