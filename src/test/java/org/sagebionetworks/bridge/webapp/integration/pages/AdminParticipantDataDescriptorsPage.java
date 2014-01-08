package org.sagebionetworks.bridge.webapp.integration.pages;

public class AdminParticipantDataDescriptorsPage extends EmbeddedSignInPage {

	public static final String TITLE = "Patient Data Descriptors Administration";
	public static final String URL = "/admin/descriptors/index.html";
	
	public AdminParticipantDataDescriptorsPage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void clickCreateDescriptors() {
		facade.click("#cbcAct");
	}

}
