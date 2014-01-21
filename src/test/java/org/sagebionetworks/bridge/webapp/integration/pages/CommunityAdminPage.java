package org.sagebionetworks.bridge.webapp.integration.pages;


public class CommunityAdminPage extends AdminPages {
	
	public static final String TITLE = "Community Administration";
	
	public CommunityAdminPage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void setName(String value) {
		facade.enterField("#name", value);
	}
	
	public void setDescription(String value) {
		facade.enterField("#description", value);
	}
	
	public void toggleAdminCheckbox(String name) {
		facade.findCheckbox(name).click();
	}
	public void assertNameError() {
		facade.assertErrorMessage("#name_errors", "A name is required");
	}
	public void assertInvalidNameError() {
		facade.assertErrorMessage("#name_errors", "Name can only contain letters, numbers, spaces, dot (.), dash (-), underscore (_) and must be at least 3 characters long.");
	}
	
	public void submit() {
		facade.submit("#communityForm");
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}

}
