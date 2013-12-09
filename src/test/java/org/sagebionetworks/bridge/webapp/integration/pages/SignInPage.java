package org.sagebionetworks.bridge.webapp.integration.pages;


public class SignInPage {
	
	public static final String TITLE = "Sign In";

	private WebDriverFacade facade;
	
	public SignInPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void signInAsTimPowers() {
		signIn("timpowers@timpowers.com", "password");
	}
	
	public void signInAsOctaviaButler() {
		signIn("octaviabutler@octaviabutler.com", "password");
	}
	
	public void signInAsJoeTest() {
		signIn("test@test.com", "password");
	}
	
	public void signIn(String email, String password) {
		if (email != null) {
			facade.waitUntil("#email");
			facade.enterField("#email", email);	
		}
		if (password != null) {
			facade.waitUntil("#password");
			facade.enterField("#password", password);
		}
		submit();
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
	
	public void clickEditProfile() {
		facade.click("#editProfileAct");
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
