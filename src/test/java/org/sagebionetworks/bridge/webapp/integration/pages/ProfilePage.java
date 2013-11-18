package org.sagebionetworks.bridge.webapp.integration.pages;


public class ProfilePage extends EmbeddedSignInPage {

	public static final String TITLE = "Profile";
	
	public ProfilePage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void setDisplayName(String value) {
		facade.enterField("#displayName", value);
	}
	public void setFirstName(String value) {
		facade.enterField("#firstName", value);
	}
	public void setLastName(String value) {
		facade.enterField("#lastName", value);
	}
	public void setDescription(String value) {
		facade.enterField("#summary", value);
	}
	public void submit() {
		facade.submit("#profileForm");
	}
}
