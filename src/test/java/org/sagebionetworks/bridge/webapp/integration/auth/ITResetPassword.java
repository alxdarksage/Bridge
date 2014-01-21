package org.sagebionetworks.bridge.webapp.integration.auth;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.TestEnvironment;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.ErrorPage;
import org.sagebionetworks.bridge.webapp.integration.pages.RequestResetPasswordPage;
import org.sagebionetworks.bridge.webapp.integration.pages.ResetPasswordPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

// TODO: When the user is not authorized with the correct token to reset password, what happens when an error is thrown?

public class ITResetPassword extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void requestPasswordResetFromDedicatedSignInPage() {
		// Just verify this exists.
		SignInPage signInPage = driver.getSignInPage();
		signInPage.clickForgotPassword();
		
		RequestResetPasswordPage page = driver.waitForRequestResetPasswordPage();
		
		page.submit();
		page.assertEmailError();
		
		page.setEmail("test@test.com");
		page.submit();
		
		driver.waitForPortalPage();
		driver.assertNotice("sent you an email. Follow the link in your email to reset your password");
	}
	
	@Test
	public void cancelPasswordResetRequest() {
		driver.getCommunityPage();
		
		SignInPage signInPage = driver.waitForSignInPage();
		signInPage.clickForgotPassword();
		
		RequestResetPasswordPage page = driver.waitForRequestResetPasswordPage();
		page.clickCancel();
		
		driver.waitForSignInPage();
	}
	
	@Test
	public void requestPasswordResetFromEmbeddedSignIn() {
		CommunityPage communityPage = driver.getCommunityPage();
		communityPage.getEmbeddedSignIn().clickForgotPassword();
		driver.waitForRequestResetPasswordPage();
		
		// The rest should be the same
	}
	
	@Test
	public void resetPasswordRequiresToken() {
		ResetPasswordPage rpPage = driver.getResetPasswordPage(false);
		
		rpPage.setPassword("antwerp");
		rpPage.setPasswordConfirm("antwerp");
		rpPage.submit();
		rpPage.assertMissingTokenError();
	}
	
	@Test
	public void resetPasswordValidatesPassword() {
		ResetPasswordPage page = driver.getResetPasswordPage(true);
		
		page.submit();
		driver.waitForResetPasswordPage();
		page.assertPasswordFormatError();
		page.assertPasswordMissingError();
		
		page.setPassword("antwerp");
		page.setPasswordConfirm("antwerp2");
		page.submit();
		driver.waitForResetPasswordPage();
		page.assertPasswordMismatchError();
	}
	
	@Test
	public void resetPasswordFormWorks() {
		// Why: You need a valid token from Synapse to go all the way through this use case,
		// and that is sent via email (or not) and not available.
		if (TestEnvironment.isUI()) {
			// Can only do this where we can fake the token, otherwise you are not authorized, it fails
			ResetPasswordPage page = driver.getResetPasswordPage(true);
			page.setPassword("antwerp");
			page.setPasswordConfirm("antwerp");
			page.submit();

			// unauthorized is one possibility
			ErrorPage error = driver.waitForErrorPage();
			error.assertErrorTitle("Unauthorized");
			
			driver.waitForPortalPage();
			driver.assertNotice("Your password has been changed.");
		}
	}
}
