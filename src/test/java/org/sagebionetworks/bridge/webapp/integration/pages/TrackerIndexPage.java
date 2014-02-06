package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TrackerIndexPage {
	
	public static final String HEADER = "Complete Blood Count";

	protected WebDriverFacade facade;
	private DataTableInPage dataTable;
	
	public TrackerIndexPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public DataTableInPage getDataTable() {
		if (dataTable == null) {
			dataTable = new DataTableInPage(facade, "#dynamicForm");
		}
		return dataTable;
	}
	
	public TrackerEditPage clickNewTrackerButton() {
		facade.click("#newTrackerAct");
		facade.waitForHeader(TrackerEditPage.NEW_HEADER);
		return new TrackerEditPage(facade);
	}
	
	public TrackerEditPage clickResumeTrackerButton() {
		facade.click("#resumeAct");
		facade.waitForHeader(TrackerEditPage.EDIT_HEADER);
		return new TrackerEditPage(facade);
	}
	
	public void clickExportButton() {
		facade.click("#exportAct");
	}
	
	public TrackerShowPage getMostRecentEntry() {
		getDataTable().clickFirstRow();
		facade.waitForHeader(TrackerShowPage.HEADER);
		return new TrackerShowPage(facade);
	}
	
	public void assertCorrectHeader() {
		facade.assertHeader("Complete Blood Count");
	}
	
	public void assertControlsForAllTrackersComplete() {
		facade.waitUntil("#newTrackerAct");
		List<WebElement> elements = facade.findElements(By.cssSelector("#resumeAct"));
		Assert.assertEquals(0, elements.size());
	}
	
	public void assertControlsForAnUnfinishedTracker() {
		facade.waitUntil("#resumeAct");
		facade.waitUntil("#newTrackerAct");
	}
}
