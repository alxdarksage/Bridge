package org.sagebionetworks.bridge.webapp.integration.pages;


/**
 * Pretty much exactly like the dedicated sign in form.
 *
 */
public class SignOutPage {
	
	public static final String TITLE = "Signed Out";
	public static final String URL = "/signOut.html";

	private WebDriverFacade facade;
	
	public SignOutPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void login(String email, String password) {
		if (email != null) {
			facade.enterField("#email", email);	
		}
		if (password != null) {
			facade.enterField("#password", password);
		}
		facade.submit("#signInForm");
	}
	
	public void submit() {
		facade.submit("#signInForm");
	}
	
	public void clickForgotPassword() {
		facade.click("#forgotPasswordLink");
	}
	
	public void clickSignUp() {
		facade.click("#signUpLink");
	}
	
	public void assertGlobalError() {
		facade.assertErrorMessage("#signInForm_errors", "Unable to sign in. Email or password may be incorrect.");
	}
	
	public void assertEmailError() {
		facade.assertErrorMessage("#email_errors", "Enter a valid email address");
	}
	
	public void assertPasswordError() {
		facade.assertErrorMessage("#password_errors", "Enter your password");
	}
}

