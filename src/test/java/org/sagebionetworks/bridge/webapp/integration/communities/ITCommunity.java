package org.sagebionetworks.bridge.webapp.integration.communities;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunitiesAdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityAdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.ErrorPage;
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
		profilePage.getProfilePanelPage().clickSignOut();
		
		cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().signInAsJoeTest();
		
		profilePage = driver.getProfilePage();
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
		page.getProfilePanelPage().clickSignOut();
		
		// Add another admin
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsJoeTest();
		page.clickJoinButton();
		page.getProfilePanelPage().clickSignOut();
		
		// Now return as Tim Powers and try and remove yourself
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsTimPowers();
		
		CommunitiesAdminPage adminPage = driver.getCommunitiesAdminPage();
		CommunityAdminPage capage = adminPage.getCommunityAdminPage();
		capage.toggleAdminCheckbox("test");
		capage.submit();
		
		// Now, should be able to leave community
		CommunitiesAdminPage caspage = driver.waitForCommunitiesAdminPage();
		caspage.getProfilePanelPage().clickEditProfile();
		
		profilePage = driver.waitForProfilePage();
		profilePage.toggleCommunityCheckbox("Fanconi Anemia");
		profilePage.submit();
		driver.assertNotice("Profile updated");
		
		// Whew, now we need to undo all of that...
		caspage = driver.waitForCommunitiesAdminPage();
		caspage.getProfilePanelPage().clickSignOut();

		// tim has to rejoin the community.
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsTimPowers();
		page.clickJoinButton();
		page.getProfilePanelPage().clickSignOut();
		
		// test has to add back tim as an admin
		page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsJoeTest();
		CommunitiesAdminPage casPage = driver.getCommunitiesAdminPage();
		capage = casPage.getCommunityAdminPage();
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
	
	@Test
	public void invalidCommunityIdRedirectsToErrorPage() {
		driver.get("/communities/40000.html");
		ErrorPage page = driver.waitForErrorPage();
		page.assertErrorTitle("Not Found");
	}
	
}
