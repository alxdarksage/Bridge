package org.sagebionetworks.bridge.webapp.integration.communities;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITCommunityWiki extends WebDriverBase {

	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void pass() {
		// avoid no runnable methods error for the moment
	}
	
}
