package org.sagebionetworks.bridge.webapp.integration.pages;

import org.openqa.selenium.By;

public class TrackerIndexPage {
	
	public static final String HEADER = "Complete Blood Count";

	protected WebDriverFacade facade;
	private DataTableInPage dataTable;
	
	public TrackerIndexPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public DataTableInPage getDataTable() {
		if (dataTable == null) {
			dataTable = new DataTableInPage(facade);
		}
		return dataTable;
	}
	
	public void assertCorrectHeader() {
		facade.assertHeader("Complete Blood Count");
	}
	
	public int getRowCount() {
		return facade.findElements(By.cssSelector("#dynamicForm table tbody tr")).size();
	}
	
	public void clickLastRow() {
		int count = getRowCount();
		facade.click("#row"+Integer.toString(count-1) + " a");
	}
	
	public TrackerEditPage clickNewTrackerButton() {
		facade.click("#newTrackerAct");
		facade.waitForHeader(TrackerEditPage.NEW_HEADER);
		return new TrackerEditPage(facade);
	}
	
	public TrackerShowPage getMostRecentEntry() {
		clickLastRow();
		facade.waitForHeader(TrackerShowPage.HEADER);
		return new TrackerShowPage(facade);
	}
	
}
