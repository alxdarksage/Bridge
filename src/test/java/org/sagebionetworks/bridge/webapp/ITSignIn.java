package org.sagebionetworks.bridge.webapp;

import org.junit.Before;
import org.junit.Test;

public class ITSignIn extends WebDriverBase {
	
	public static class SignInDriver extends WebDriverFacade {
		
		public SignInDriver(WebDriverFacade facade) {
			super(facade);
		}
		public void email(String value) {
			super.enterField("#email", value);
		}
		public void password(String value) {
			super.enterField("#password", value);
		}
		public void submit() {
			super.submit("#signInForm");
		}
	}
	
	private SignInDriver driver;

	@Before
	public void createDriver() {
		driver = new SignInDriver(initDriver());
	}
	
	@Test
	public void dedicatedSignInFormRejectsEmptyForm() {
		driver.get("/signIn.html");
		
		driver.submit();
		
		driver.waitForErrorOn("email", "Enter a valid email address");
		driver.waitForErrorOn("password", "Enter your password");
	}
	
	@Test
	public void dedicatedSignInFormRejectsInvalidEmailAddress() {
		driver.get("/signIn.html");
		
		driver.email("timpowers");
		driver.submit();
		
		driver.waitForErrorOn("email", "Enter a valid email address");
		driver.waitForErrorOn("password", "Enter your password");
	}
	
	@Test
	public void dedicatedSignInFormRejectsUnregisteredUser() {
		driver.get("/signIn.html");
		
		driver.email("dudeski@dudeski.com");
		driver.password("password");
		driver.submit();
		
		driver.waitForErrorOn("signInForm");
		driver.assertErrorMessage("#signInForm_errors", "Unable to sign in. Email or password may be incorrect.");
	}
	
	@Test
	public void dedicatedSignInFormRedirectsCorrectly() {
		driver.get("/communities/index.html");
		driver.get("/signIn.html");
		
		driver.email("timpowers@timpowers.com");
		driver.password("password");
		driver.submit();
		driver.waitForCommunityPage();
		
		// Once logged in, you are always redirected from this page.
		driver.get("/signIn.html");
		driver.waitForCommunityPage();
	}
	
	@Test
	public void youAreRedirectedToLogInIfYouAreNot() {
		driver.get("/profile.html");
		driver.waitForSignInPage();
		
		driver.email("timpowers@timpowers.com");
		driver.password("password");
		driver.submit();
		driver.waitForProfilePage();
	}
	
	@Test
	public void dedicatedSignInFormLinksToIForgotMyPassword() {
		driver.get("/signIn.html");
		driver.click("#forgotPasswordLink");
		driver.waitForResetPasswordPage();
	}
	
	@Test
	public void dedicatedSignInFormToSignUp() {
		driver.get("/signIn.html");
		driver.click("#signUpLink");
		driver.waitForSignUpPage();
	}	
	
	@Test
	public void dedicatedSignInFormRedirectsForTOU() {
		driver.get("/communities/index.html");
		driver.get("/signIn.html");
		
		driver.email("octaviabutler@octaviabutler.com");
		driver.password("password");
		driver.submit();
		
		// Octavia has not signed the TOU.
		driver.waitForTOUPage();
	}
	
	@Test
	public void embeddedSignInFormRejectsEmptyForm() {
		driver.get("/communities/index.html");
		driver.submit();
		driver.waitForError("Unable to sign in. Email or password may be incorrect.");
	}
	
	@Test
	public void embeddedSignInFormRejectsInvalidEmailAddress() {
		driver.get("/communities/index.html");
		driver.email("timpowers");
		// This really isn't ideal, why are we redirecting? I can't remember.
		driver.submit();
		driver.waitForError("Unable to sign in. Email or password may be incorrect.");	
	}
	
	@Test
	public void embeddedSignInFormRejectsUnregisteredUser() {
		driver.get("/communities/index.html");
		driver.email("dudeski@dudeski.com");
		driver.password("password");
		driver.submit();
		driver.waitForError("Unable to sign in. Email or password may be incorrect.");		
	}
	
	@Test
	public void embeddedSignInFormRedirectsCorrectly() {
		driver.get("/index.html");
		driver.get("/communities/index.html");
		driver.email("timpowers@timpowers.com");
		driver.password("password");
		driver.submit();
		driver.waitForCommunityPage();
	}
	
	@Test
	public void embeddedSignInFormLinksToIForgotMyPassword() {
		driver.get("/communities/index.html");
		driver.click("#forgotPasswordLink");
		driver.waitForResetPasswordPage();
	}
	
	@Test
	public void embeddedSignInFormToSignUp() {
		driver.get("/communities/index.html");
		driver.click("#signUpLink");
		driver.waitForSignUpPage();
	}
	
	@Test
	public void embeddedSignInFormRedirectsForTOU() {
		driver.get("/communities/index.html");
		driver.enterField("#email", "octaviabutler@octaviabutler.com");
		driver.enterField("#password", "password");
		driver.submit();
		// Octavia will never sign the TOU (in the stub client).
		driver.waitForTOUPage();
	}
	
}
