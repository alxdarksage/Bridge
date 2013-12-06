package org.sagebionetworks.bridge.webapp.integration.auth;

import org.junit.Assert;
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
		
		driver.getCommunityPage();
		
		ProfilePage ppage = driver.getProfilePage();
		ppage.submit();
		
		driver.waitForCommunityPage();
		driver.assertNotice("Profile updated");
	}
	
	@Test
	public void profilePageRedirectsAfterCancel() {
		SignInPage page = driver.getSignInPage();
		page.signInAsTimPowers();
		
		driver.getCommunityPage();
		
		ProfilePage ppage = driver.getProfilePage();
		ppage.clickCancel();
		
		driver.waitForCommunityPage();
	}
	
	@Test
	public void cannotRemoveSelfAsLastAdministratorFromCommunities() {
		// TODO: This seems to work in the stub, not sure what happens in the real client
	}
	
}
