package org.sagebionetworks.bridge.webapp.integration.portal;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.AdminPage;
import org.sagebionetworks.bridge.webapp.integration.pages.PortalPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITPortal extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void redirectsFromIndex() {
		driver.get("/");
		driver.waitForPortalPage();
	}
	
	@Test
	public void adminSectionHiddenForNonAdmins() {
		PortalPage portalPage = driver.getPortalPage();
		portalPage.getEmbeddedSignIn().signInAsTimPowers(); // who is an admin
		portalPage.clickAdmin();
		
		AdminPage adminPage = driver.waitForAdminPage();
		adminPage.getProfilePanelPage().clickSignOut();
		
		portalPage = driver.getPortalPage();
		portalPage.getEmbeddedSignIn().signInAsNotAnAdminUser();
		
		portalPage.assertAdminLinkMissing();
	}
	
}
