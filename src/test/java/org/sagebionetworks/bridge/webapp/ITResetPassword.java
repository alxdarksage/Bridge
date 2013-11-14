package org.sagebionetworks.bridge.webapp;

import org.junit.Before;
import org.junit.Test;

// TODO: When the user is not authorized with the correct token to reset password, what happens when an error is thrown?

public class ITResetPassword extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void forgotPasswordFromDedicatedSignIn() {
		driver.get("/signIn.html");
		driver.click("#forgotPasswordLink");
		driver.waitForResetPasswordPage();
		
		driver.submit("#requestResetPasswordForm");
		driver.waitForErrorOn("email", "Enter a valid email address");
		
		driver.enterField("#email", "test@test.com");
		driver.submit("#requestResetPasswordForm");
		driver.waitForPortalPage();
		driver.waitForAndAssertNotice("sent you an email. Follow the link in your email to reset your password");
	}
	
	@Test
	public void forgotPasswordFromEmbeddedSignIn() {
		// This doesn't work in Chrome because Chrome opens the page narrow enough that the sidebar
		// does not show, and then it throws an error. Which is odd, but it makes sense.
		driver.get("/communities/index.html");
		driver.click("#forgotPasswordLink");
		driver.waitForResetPasswordPage();
		
		driver.submit("#requestResetPasswordForm");
		driver.waitForErrorOn("email", "Enter a valid email address");
		
		driver.enterField("#email", "test@test.com");
		driver.submit("#requestResetPasswordForm");
		driver.waitForCommunityPage();
		driver.waitForAndAssertNotice("sent you an email. Follow the link in your email to reset your password");
		
	}
	
	@Test
	public void testFormRequiresSynapseToken() {
		driver.get("/resetPassword.html");
		driver.submit("#resetPasswordForm");
		driver.assertErrorMessage("#token_errors", "a link and further instructions on how to change it");
	}
	
	@Test
	public void resetPasswordFormWorks() {
		driver.get("/resetPassword.html?token=belgium");
		driver.submit("#resetPasswordForm");
		
		driver.assertErrorMessage("#password_errors", "Password should be 6-30 letters long");
		driver.assertErrorMessage("#password_errors", "Enter a password");
		driver.assertErrorMessage("#passwordConfirm_errors", "Re-enter the password");
		
		driver.enterField("#password", "asdfasdf");
		driver.enterField("#passwordConfirm", "asdfasdfasdf");
		driver.submit("#resetPasswordForm");
		
		driver.assertErrorMessage("#resetPasswordForm_errors", "The password confirmation is not the same as the password.");
		
		driver.enterField("#password", "asdfasdf");
		driver.enterField("#passwordConfirm", "asdfasdf");
		driver.submit("#resetPasswordForm");
		driver.waitForPortalPage();
		
		driver.waitForAndAssertNotice("Your password has been changed.");		
	}
}
