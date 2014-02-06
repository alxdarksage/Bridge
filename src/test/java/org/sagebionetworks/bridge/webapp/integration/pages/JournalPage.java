package org.sagebionetworks.bridge.webapp.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sagebionetworks.bridge.webapp.specs.trackers.CompleteBloodCount;
import org.sagebionetworks.bridge.webapp.specs.trackers.MedicationTracker;

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
	
	public void clickMedication() {
		WebElement element = facade.findElement(By.partialLinkText(new MedicationTracker().getName()));
		element.click();
	}
	
	public TrackerIndexPage getCBCIndexPage() {
		clickCompleteBloodCount();
		return waitForCBCIndexPage();
	}
	
	public TrackerIndexPage waitForCBCIndexPage() {
		facade.waitForHeader(TrackerIndexPage.HEADER);
		return new TrackerIndexPage(facade);
	}
	
	public OnePageTrackerEditPage getMedicationPage() {
		clickMedication();
		return waitForMedicationIndexPage();
	}
	
	public OnePageTrackerEditPage waitForMedicationIndexPage() {
		facade.waitForHeader(OnePageTrackerEditPage.HEADER);
		return new OnePageTrackerEditPage(facade);
	}
	
}
