package org.sagebionetworks.bridge.webapp.integration.pages;


public class CommunityAdminPage extends AdminPages {
	
	public static final String TITLE = "Community Administration";
	public static final String URL = "/admin/communities/1.html";
	
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
	
	public void submit() {
		facade.submit("#communityForm");
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}

}
