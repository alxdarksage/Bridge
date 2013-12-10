package org.sagebionetworks.bridge.webapp.integration.pages;

public class RequestResetPasswordPage {

	public static final String TITLE = "Reset Password";
	public static final String URL = "/requestResetPassword.html";
	
	private WebDriverFacade facade;
	
	public RequestResetPasswordPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void setEmail(String value) {
		facade.enterField("#email", value);
	}
	
	public void assertEmailError() {
		facade.assertErrorMessage("#email_errors", "Enter a valid email address");
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}
	
	public void submit() {
		facade.submit("#requestResetPasswordForm");
	}
	
}
