package org.sagebionetworks.bridge.webapp;

import org.junit.Before;
import org.junit.Test;

public class ITSignOut extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void signOutReturnsSignOutPage() {
		signIn();
		signOut();
		driver.waitForSignedOutPage();
	}
	
	@Test
	public void signOutCausesAuthExceptions() {
		signIn();
		signOut();
		driver.get("/profile.html");
		driver.waitForSignInPage();
	}
	
}
