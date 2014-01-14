package org.sagebionetworks.bridge.webapp.integration.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormShowPage {
	
	private static final Logger logger = LogManager.getLogger(FormShowPage.class.getName());

	public static final String HEADER = "Complete Blood Count";
	
	protected WebDriverFacade facade;

	public FormShowPage(WebDriverFacade facade) {
		this.facade = facade;
	}

	public FormEditPage clickEditSurveyButton() {
		facade.click("#editAct");
		facade.waitForHeader(FormEditPage.EDIT_HEADER);
		return new FormEditPage(facade);
	}
	
	
}
