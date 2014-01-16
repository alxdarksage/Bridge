package org.sagebionetworks.bridge.webapp.integration.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackerShowPage {
	
	private static final Logger logger = LogManager.getLogger(TrackerShowPage.class.getName());

	public static final String HEADER = "Complete Blood Count";
	
	protected WebDriverFacade facade;

	public TrackerShowPage(WebDriverFacade facade) {
		this.facade = facade;
	}

	public TrackerEditPage clickEditTrackerButton() {
		facade.click("#editAct");
		facade.waitForHeader(TrackerEditPage.EDIT_HEADER);
		return new TrackerEditPage(facade);
	}
	
	
}
