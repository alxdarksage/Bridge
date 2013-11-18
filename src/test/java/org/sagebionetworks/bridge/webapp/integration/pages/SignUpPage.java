package org.sagebionetworks.bridge.webapp.integration.pages;


public class SignUpPage {

	public static final String TITLE = "Sign Up";
	
	protected WebDriverFacade facade;
	
	public SignUpPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void setEmail(String value) {
		facade.enterField("#email", value);
	}
	public void setUserName(String value) {
		facade.enterField("#displayName", value);
	}
	public void submit() {
		facade.submit("#signUpForm");
	}
	public void assertEmailError() {
		facade.waitUntil("#email_errors");
		facade.assertErrorMessage("#email_errors", "Enter a valid email address");
	}
	public void assertUserNameError() {
		facade.waitUntil("#displayName_errors");
		facade.assertErrorMessage("#displayName_errors", "Enter a user name");
	}
	
}
