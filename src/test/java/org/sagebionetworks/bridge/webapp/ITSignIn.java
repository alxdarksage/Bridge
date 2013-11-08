package org.sagebionetworks.bridge.webapp;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ITSignIn {
	
	public class SignInDriver extends WebDriverFacade {
		
		public SignInDriver(WebDriverFacade facade) {
			super(facade.driver);
		}
		public void email(String value) {
			super.enterForm("#email", value);
		}
		public void password(String value) {
			super.enterForm("#password", value);
		}
		public SignInDriver submit() {
			super.submit("#signInForm");
			return this;
		}
		public void assertEmailError(String error) {
			super.assertErrorMessage("#email_errors", error);
		}
		public void assertPasswordError(String error) {
			super.assertErrorMessage("#password_errors", error);
		}
		public void assertGlobalError(String error) {
			super.waitUntil("div.alert-danger");
			super.assertErrorMessage("div.alert-danger", error);
		}
	}
	
	private SignInDriver driver;

	@Before
	public void createDriver() {
		driver = new SignInDriver(new WebDriverFacade(new FirefoxDriver()));
		driver.manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
	}
	
	@After
	public void closeDriver() {
		driver.assertMissing("#error-pane");
		driver.close();
		driver.quit();
	}
	
	@Test
	public void dedicatedSignInFormRejectsEmptyForm() {
		driver.get("/signIn.html");
		
		driver.submit().waitUntil("div.has-error");
		
		driver.assertEmailError("Enter a valid email address");
		driver.assertPasswordError("Enter your password");
	}
	
	@Test
	public void dedicatedSignInFormRejectsInvalidEmailAddress() {
		driver.get("/signIn.html");
		
		driver.email("timpowers");
		driver.submit().waitUntil("div.has-error");
		
		driver.assertEmailError("Enter a valid email address");
		driver.assertPasswordError("Enter your password");
	}
	
	@Test
	public void dedicatedSignInFormRejectsUnregisteredUser() {
		driver.get("/signIn.html");
		
		driver.email("dudeski@dudeski.com");
		driver.password("password");
		driver.submit().waitUntil("#signInForm_errors");
		
		driver.assertErrorMessage("#signInForm_errors", "Unable to sign in. Email or password may be incorrect.");
	}
	
	@Test
	public void dedicatedSignInFormRedirectsCorrectly() {
		driver.get("/communities/index.html");
		driver.get("/signIn.html");
		
		driver.email("timpowers@timpowers.com");
		driver.password("password");
		driver.submit().waitUntil("#profile-pane");
		
		driver.assertTitle("Bridge : Fanconi Anemia");

		// Once logged in, you are always redirected from this page.
		driver.get("/signIn.html");
		driver.assertTitle("Bridge : Fanconi Anemia");
	}
	
	/* TODO: This currently fails due to auth exception. It's safe, but unfriendly.
	@Test
	public void youAreRedirectedToLogInIfYouAreNot() {
		driver.get("/profile.html");
		driver.assertTitle("Bridge : Sign In");
		
		driver.email("timpowers@timpowers.com");
		driver.password("password");
		driver.submit().waitUntil("#profileForm");
	}
	*/
	
	@Test
	public void dedicatedSignInFormLinksToIForgotMyPassword() {
		driver.get("/signIn.html");
		driver.click("#forgotPasswordLink").waitUntil("#resetPasswordForm");
		driver.assertTitle("Bridge : Reset Password");
	}
	
	@Test
	public void dedicatedSignInFormToSignUp() {
		driver.get("/signIn.html");
		
		driver.click("#signUpLink").waitUntil("#signUpForm");
		driver.assertTitle("Bridge : Sign Up for Bridge");
	}	
	
	@Test
	public void dedicatedSignInFormRedirectsForTOU() {
		driver.get("/communities/index.html");
		driver.get("/signIn.html");
		
		driver.email("octaviabutler@octaviabutler.com");
		driver.password("password");
		
		// Octavia has not signed the TOU.
		driver.submit().waitUntil("#termsOfUseForm");
		
		// Just test that this throws an error, not worth a separate test
		driver.submit("#termsOfUseForm").waitUntil("#acceptTermsOfUse_errors");
		driver.assertErrorMessage("#acceptTermsOfUse_errors", "You must accept the terms of use before signing in to Bridge.");
		
		driver.click("#acceptTermsOfUse");
		
		driver.submit("#termsOfUseForm").waitUntil("#profile-pane");
		driver.assertExists("#signOutButton");
	}
	
	@Test
	public void embeddedSignInFormRejectsEmptyForm() {
		driver.get("/communities/index.html");
		
		driver.submit();
		
		driver.assertGlobalError("Unable to sign in. Email or password may be incorrect.");	
	}
	
	@Test
	public void embeddedSignInFormRejectsInvalidEmailAddress() {
		driver.get("/communities/index.html");
		
		driver.email("timpowers");
		
		// This really isn't ideal, why are we redirecting? I can't remember.
		driver.submit();
		
		driver.assertGlobalError("Unable to sign in. Email or password may be incorrect.");	
	}
	
	@Test
	public void embeddedSignInFormRejectsUnregisteredUser() {
		driver.get("/communities/index.html");
		
		driver.email("dudeski@dudeski.com");
		driver.password("password");

		driver.submit();
		
		driver.assertGlobalError("Unable to sign in. Email or password may be incorrect.");		
	}
	
	@Test
	public void embeddedSignInFormRedirectsCorrectly() {
		driver.get("/index.html");
		driver.get("/communities/index.html");
		
		driver.email("timpowers@timpowers.com");
		driver.password("password");
		
		driver.submit().waitUntil("#profile-pane");
		driver.assertTitle("Bridge : Fanconi Anemia");
	}
	
	@Test
	public void embeddedSignInFormLinksToIForgotMyPassword() {
		driver.get("/communities/index.html");
		driver.click("#forgotPasswordLink").waitUntil("#resetPasswordForm");
		driver.assertTitle("Bridge : Reset Password");
	}
	
	@Test
	public void embeddedSignInFormToSignUp() {
		driver.get("/communities/index.html");
		driver.click("#signUpLink").waitUntil("#signUpForm");
		driver.assertTitle("Bridge : Sign Up for Bridge");
	}
	
	@Test
	public void embeddedSignInFormRedirectsForTOU() {
		driver.get("/communities/index.html");
		
		driver.enterForm("#email", "octaviabutler@octaviabutler.com");
		driver.enterForm("#password", "password");
		
		// Octavia has not signed the TOU.
		driver.submit().waitUntil("#termsOfUseForm");
		
		// Just test that this throws an error, not worth a separate test
		driver.submit("#termsOfUseForm").waitUntil("#acceptTermsOfUse_errors");
		driver.assertErrorMessage("#acceptTermsOfUse_errors", "You must accept the terms of use before signing in to Bridge.");
		
		driver.click("#acceptTermsOfUse");
		
		driver.submit("#termsOfUseForm").waitUntil("#profile-pane");
		driver.assertExists("#signOutButton");
	}
	
}
