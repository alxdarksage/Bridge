package org.sagebionetworks.bridge.webapp.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sagebionetworks.bridge.webapp.specs.trackers.CompleteBloodCount;

public class JournalPage {

	public static final String TITLE = "Journal";
	public static final String URL = "/journal.html";
	
	protected WebDriverFacade facade;

	public JournalPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void clickCompleteBloodCount() {
		WebElement element = facade.findElement(By.partialLinkText(new CompleteBloodCount().getName()));
		element.click();
	}
	
	public TrackerIndexPage getTrackerIndexPage() {
		clickCompleteBloodCount();
		return waitForTrackerIndexPage();
	}
	
	public TrackerIndexPage waitForTrackerIndexPage() {
		facade.waitForHeader(TrackerIndexPage.HEADER);
		return new TrackerIndexPage(facade);
	}
}
