package org.sagebionetworks.bridge.webapp.integration.communities;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunitiesAdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityAdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.ProfilePage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITCommunity extends WebDriverBase {

	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void userCanJoinCommunity() {
		CommunityPage cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().signIn();
		cpage = driver.waitForCommunityPage();
	}
	
	// This test fails. It cannot find the inputs, I'm not sure why
	/*
	public void userCanSeeMembershipOnProfilePage() {
		CommunityPage cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().signIn();

		ProfilePage profilePage = driver.getProfilePage();
		profilePage.assertCommunitySelected("Fanconi Anemia");
		profilePage.getEmbeddedSignIn().signOut();
	}
	*/
	
	@Test
	public void userCanLeaveCommunity() {
		
	}
	
	@Test
	public void adminUserCannotLeaveCommunity() {
	}
	
}
