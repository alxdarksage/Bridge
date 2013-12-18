package org.sagebionetworks.bridge.webapp.integration.portal;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
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
	
}
