package org.sagebionetworks.bridge.webapp.integration.communities;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.controllers.admin.CommunitiesAdminController;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunitiesAdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityAdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.ProfilePage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITCommunity extends WebDriverBase {

	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void adminCanSeeEditWikiButton() {
		CommunityPage page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsTimPowers();
		page.assertEditButtonPreset();
		page.assertJoinButtonNotPreset();
	}
	
	@Test
	public void userCanSeeJoinButton() {
		CommunityPage page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsJoeTest();
		page.assertEditButtonNotPreset();
		page.assertJoinButtonPreset();
	}
	
	public void userCanSeeMembershipOnProfilePage() {
		CommunityPage cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().signInAsTimPowers();

		ProfilePage profilePage = driver.getProfilePage();
		profilePage.assertCommunitySelected("Fanconi Anemia");
		profilePage.getEmbeddedSignIn().signOut();
		
		cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().signInAsJoeTest();
		profilePage.assertCommunityNotSelected("Fanconi Anemia");
	}
	
	@Test
	public void userCanJoinAndLeaveCommunity() {
		CommunityPage page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsJoeTest();
		
		page = driver.waitForCommunityPage();
		page.clickJoinButton();

		page = driver.waitForCommunityPage();
		page.assertEditButtonNotPreset();
		page.assertJoinButtonNotPreset();
		
		ProfilePage ppage = driver.getProfilePage();
		ppage.toggleCommunityCheckbox("Fanconi Anemia");
		ppage.submit();
		
		page = driver.waitForCommunityPage();
		page.assertEditButtonNotPreset();
		page.assertJoinButtonPreset();
	}
	
	@Test
	public void lastAdminUserCannotLeaveCommunity() {
		CommunityPage page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsTimPowers();
		
		// With one admin, you are prevented.
		ProfilePage profilePage = driver.getProfilePage();
		profilePage.toggleCommunityCheckbox("Fanconi Anemia");
		profilePage.submit();
		profilePage.assertNeedAtLeastOneAdminError();
		
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signOut();
		
		// Add another admin
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsJoeTest();
		page.clickJoinButton();
		page.getEmbeddedSignIn().signOut();
		
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsTimPowers();
		
		CommunityAdminPage capage = driver.getCommunityAdminPage();
		capage.toggleAdminCheckbox("test");
		capage.submit();
		
		// Now, should be able to leave community
		CommunitiesAdminPage caspage = driver.waitForCommunitiesAdminPage();
		caspage.getEmbeddedSignIn().clickEditProfile();
		
		profilePage = driver.waitForProfilePage();
		profilePage.toggleCommunityCheckbox("Fanconi Anemia");
		profilePage.submit();
		driver.assertNotice("Profile updated");
		
		// Whew, now we need to undo all of that...
		caspage = driver.waitForCommunitiesAdminPage();
		caspage.getEmbeddedSignIn().signOut();

		// tim has to rejoin the community.
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsTimPowers();
		page.clickJoinButton();
		page.getEmbeddedSignIn().signOut();
		
		// test has to add back tim as an admin
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsJoeTest();
		
		capage = driver.getCommunityAdminPage();
		capage.toggleAdminCheckbox("timpowers");
		capage.submit();
		
		// test can then unjoin the community
		profilePage = driver.getProfilePage();
		profilePage.toggleCommunityCheckbox("Fanconi Anemia");
		profilePage.submit();
		driver.assertNotice("Profile updated");
		
		page = driver.getCommunityPage();
		page.assertEditButtonNotPreset();
		page.assertJoinButtonPreset();
	}
	
}
