package org.sagebionetworks.bridge.webapp.integration.pages;


public class ErrorPage {
	
	private WebDriverFacade facade;
	
	public ErrorPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void assertErrorTitle(String title) {
		facade.assertErrorMessage("h3#error-pane", title);
	}
	

}
