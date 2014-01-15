package org.sagebionetworks.bridge.webapp.integration.pages;


public class SignInPage {
	
	public static final String TITLE = "Sign In";
	public static final String URL = "/signIn.html";

	private WebDriverFacade facade;
	
	public SignInPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void signInAsTimPowers() {
		signIn("timpowers", "password");
	}
	
	public void signInAsOctaviaButler() {
		signIn("octaviabutler", "password");
	}
	
	public void signInAsJoeTest() {
		signIn("test", "password");
	}
	
	public void signIn(String userName, String password) {
		if (userName != null) {
			facade.waitUntil("#userName");
			facade.enterField("#userName", userName);	
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
