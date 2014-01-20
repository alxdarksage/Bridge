package org.sagebionetworks.bridge.webapp.integration.admin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.AdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunitiesAdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityAdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITCommunityAdmin extends WebDriverBase {

	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
		SignInPage signInPage = driver.getSignInPage();
		signInPage.signInAsTimPowers();
	}

	@Test
	public void rowCanBeSelected() {
		CommunitiesAdminPage caPage = driver.getCommunitiesAdminPage();
		caPage.assertDeleteDisabled();
		caPage.selectRow("Fanconi Anemia");
		caPage.assertDeleteEnabled();
		caPage.assertRowSelected("Fanconi Anemia");
		
		caPage.selectRow("Fanconi Anemia");
		caPage.assertDeleteDisabled();
	}
	
	@Test
	public void masterCheckboxTogglesRows() {
		CommunitiesAdminPage caPage = driver.getCommunitiesAdminPage();
		
		caPage.assertDeleteDisabled();
		caPage.assertAllRowsDeselected();
		
		caPage.clickMasterCheckbox();
		caPage.assertDeleteEnabled();
		caPage.assertAllRowsSelected();
		
		caPage.clickMasterCheckbox();
		caPage.assertDeleteDisabled();
		caPage.assertAllRowsDeselected();
	}
	
	@Test
	public void createCommunityValidatesData() {
		AdminPage adminPage = driver.getAdminPage();
		
		adminPage.clickCommunities();
		CommunitiesAdminPage casPage = driver.waitForCommunitiesAdminPage();
		CommunityAdminPage communityPage = casPage.clickNewCommunity();
		
		communityPage.submit();
		communityPage.assertNameError();
	}
	
	@Test
	public void createAndDeleteCommunity() {
		// NOTE: This test is a problem because if there's a failure, then 
		// there is this community in the db, and it'll fail. 
		CommunitiesAdminPage casPage = driver.getCommunitiesAdminPage();
		CommunityAdminPage communityPage = casPage.clickNewCommunity();
		
		// Create
		communityPage.setName("Test Community");
		communityPage.setDescription("This is a test community");
		communityPage.submit();
		casPage = driver.waitForCommunitiesAdminPage();		
		driver.assertNotice("Community created.");
		
		// Delete
		casPage.selectRow("Test Community");
		casPage.clickDelete();
		driver.assertNotice("Community deleted.");
		Assert.assertFalse(casPage.rowExists("Test Community"));
	}
	
	@Test
	public void canCancelCreateCommunity() {
		CommunitiesAdminPage casPage = driver.getCommunitiesAdminPage();
		CommunityAdminPage communityPage = casPage.clickNewCommunity();
		
		communityPage.submit(); // just to verify it works even after a post
		communityPage.clickCancel();
		
		casPage = driver.waitForCommunitiesAdminPage();
	}
}
