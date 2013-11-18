package org.sagebionetworks.bridge.webapp.integration.pages;

public class ResetPasswordPage {

	public static final String TITLE = "Reset Password";
	
	private WebDriverFacade facade;
	
	public ResetPasswordPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void setPassword(String value) {
		facade.enterField("#password", value);
	}
	
	public void setPasswordConfirm(String value) {
		facade.enterField("#passwordConfirm", value);
	}

	public void assertPasswordFormatError() {
		facade.assertErrorMessage("#password_errors", "Password should be 6-30 letters long");
	}
	
	public void assertPasswordMissingError() {
		facade.assertErrorMessage("#password_errors", "Enter a password");
	}
	
	public void assertPasswordConfirmError() {
		facade.assertErrorMessage("#passwordConfirm_errors", "Re-enter the password");
	}
	
	public void assertPasswordMismatchError() {
		facade.assertErrorMessage("#resetPasswordForm_errors",
			"The password confirmation is not the same as the password.");
	}
	
	public void assertMissingTokenError() {
		facade.assertErrorMessage("#token_errors",
			"with a link and further instructions on how to change it.");
	}
	
	public void submit() {
		facade.submit("#resetPasswordForm");
	}
	
}
