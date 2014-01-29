package org.sagebionetworks.bridge.webapp.integration.auth;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.ProfilePage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITProfile extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void profilePageRedirectsAfterSave() {
		SignInPage page = driver.getSignInPage();
		page.signInAsTimPowers();
		
		driver.waitForPortalPage();
		driver.getCommunityPage();
		driver.takeScreenshot("profilePageRedirectsAfterSave-26");
		ProfilePage ppage = driver.getProfilePage();
		driver.takeScreenshot("profilePageRedirectsAfterSave-28");
		ppage.submit();
		driver.takeScreenshot("profilePageRedirectsAfterSave-30");
		driver.waitForCommunityPage();
		driver.takeScreenshot("profilePageRedirectsAfterSave-32");
		driver.assertNotice("Profile updated");
		driver.takeScreenshot("profilePageRedirectsAfterSave-34");
	}
	
	@Test
	public void profilePageRedirectsAfterCancel() {
		SignInPage page = driver.getSignInPage();
		page.signInAsTimPowers();
		
		driver.waitForPortalPage();
		driver.getCommunityPage();
		
		driver.takeScreenshot("profilePageRedirectsAfterCancel-45");
		ProfilePage ppage = driver.getProfilePage();
		driver.takeScreenshot("profilePageRedirectsAfterCancel-47");
		ppage.clickCancel();
		driver.takeScreenshot("profilePageRedirectsAfterCancel-49");
		
		driver.waitForCommunityPage();
	}
	
}
