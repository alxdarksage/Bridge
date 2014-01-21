package org.sagebionetworks.bridge.webapp.integration.pages;


public class SignUpPage {

	public static final String TITLE = "Sign Up";
	public static final String URL = "/signUp.html";
	
	protected WebDriverFacade facade;
	
	public SignUpPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void setEmail(String value) {
		facade.enterField("#email", value);
	}
	public void setUserName(String value) {
		facade.enterField("#userName", value);
	}
	public void submit() {
		facade.submit("#signUpForm");
	}
	public void assertEmailError() {
		facade.assertErrorMessage("#email_errors", "Enter a valid email address");
	}
	public void assertUserNameError() {
		facade.assertErrorMessage("#userName_errors", "Enter a user name");
	}
	public void assertEmailDuplicateError() {
		facade.assertErrorMessage("#email_errors", "User email has been taken");
	}
	public void assertUserNameDuplicateError() {
		facade.assertErrorMessage("#userName_errors", "User name has been taken");
	}
}
