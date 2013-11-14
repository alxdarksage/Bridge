package org.sagebionetworks.bridge.webapp;

import org.junit.Before;
import org.junit.Test;

public class ITProfile extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void profilePageRedirectsAfterSave() {
		signIn("timpowers@timpowers.com", "password");
		driver.get("/profile.html");
		driver.submit("#profileForm");
		driver.waitForCommunityPage();
		driver.waitForAndAssertNotice("Profile updated");
	}
	
}
