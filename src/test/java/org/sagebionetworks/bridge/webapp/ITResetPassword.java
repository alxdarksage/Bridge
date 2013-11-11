package org.sagebionetworks.bridge.webapp;

import org.junit.Before;
import org.junit.Test;

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
		
		driver.submit("#resetPasswordForm");
		driver.waitForErrorOn("email", "Enter a valid email address");
		
		driver.enterField("#email", "test@test.com");
		driver.submit("#resetPasswordForm");
		driver.waitForPortalPage();
		driver.waitForNotice("sent you an email. Follow the link in your email to reset your password");
	}
	
	@Test
	public void forgotPasswordFromEmbeddedSignIn() {
		// This doesn't work in Chrome because Chrome opens the page narrow enough that the sidebar
		// does not show, and then it throws an error. Which is odd, but it makes sense.
		driver.get("/communities/index.html");
		driver.click("#forgotPasswordLink");
		driver.waitForResetPasswordPage();
		
		driver.submit("#resetPasswordForm");
		driver.waitForErrorOn("email", "Enter a valid email address");
		
		driver.enterField("#email", "test@test.com");
		driver.submit("#resetPasswordForm");
		driver.waitForCommunityPage();
		driver.waitForNotice("sent you an email. Follow the link in your email to reset your password");
		
	}
}
