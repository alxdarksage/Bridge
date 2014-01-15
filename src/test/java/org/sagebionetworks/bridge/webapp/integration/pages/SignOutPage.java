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
	
	public void login(String userName, String password) {
		if (userName != null) {
			facade.enterField("#userName", userName);	
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
		facade.assertErrorMessage("#signInForm_errors", "Unable to sign in. User name or password may be incorrect.");
	}
	
	public void assertUserNameError() {
		facade.assertErrorMessage("#userName_errors", "Enter a valid user name");
	}
	
	public void assertPasswordError() {
		facade.assertErrorMessage("#password_errors", "Enter your password");
	}
}

