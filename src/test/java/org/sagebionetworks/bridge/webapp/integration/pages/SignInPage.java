package org.sagebionetworks.bridge.webapp.integration.pages;


public class SignInPage {
	
	public static final String TITLE = "Sign In";

	private WebDriverFacade facade;
	
	public SignInPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void signIn() {
		signIn("timpowers@timpowers.com", "password");
	}
	
	public void signIn(String email, String password) {
		if (email != null) {
			facade.enterField("#email", email);	
		}
		if (password != null) {
			facade.enterField("#password", password);
		}
		facade.submit("#signInForm");
	}
	
	public void signOut() {
		facade.click("#signOutButton");
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
	
	public void clickSignOut() {
		facade.click("#signOutButton");
	}
	
	public void assertGlobalError() {
		facade.waitUntil("#signInForm_errors");
		facade.assertErrorMessage("#signInForm_errors", "Unable to sign in. Email or password may be incorrect.");
	}
	
	public void assertEmailError() {
		facade.waitUntil("#email_errors");
		facade.assertErrorMessage("#email_errors", "Enter a valid email address");
	}
	
	public void assertPasswordError() {
		facade.waitUntil("#password_errors");
		facade.assertErrorMessage("#password_errors", "Enter your password");
	}
	
}
