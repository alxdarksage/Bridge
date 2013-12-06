package org.sagebionetworks.bridge.webapp.integration.auth;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITSignOut extends WebDriverBase {
	
	private WebDriverFacade facade;

	@Before
	public void createDriver() {
		facade = initDriver();
	}
	
	@Test
	public void signOutReturnsSignOutPage() {
		CommunityPage communityPage = facade.getCommunityPage();
		communityPage.getEmbeddedSignIn().signInAsTimPowers();
		
		communityPage.getEmbeddedSignIn().signOut();
		facade.waitForSignedOutPage();
	}
	
	@Test
	public void signOutCausesAuthExceptions() {
		CommunityPage communityPage = facade.getCommunityPage();
		communityPage.getEmbeddedSignIn().signInAsTimPowers();
		
		communityPage = facade.waitForCommunityPage();
		communityPage.getEmbeddedSignIn().signOut();
		
		facade.getProfilePage();
		facade.waitForSignInPage();
	}
	
}
