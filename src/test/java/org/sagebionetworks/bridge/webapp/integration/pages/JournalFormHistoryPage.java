package org.sagebionetworks.bridge.webapp.integration.pages;

import org.openqa.selenium.By;

public class JournalFormHistoryPage {
	
	public static final String HEADER = "Complete Blood Count";

	protected WebDriverFacade facade;

	public JournalFormHistoryPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void assertCorrectHeader() {
		facade.assertHeader("Complete Blood Count");
	}
	
	public void clickNewSurveyButton() {
		facade.click("#newSurveyAct");
	}
	
	public int getRowCount() {
		return facade.findElements(By.cssSelector("table.table tbody tr")).size();
	}
	
	public void clickLastRow() {
		int count = getRowCount();
		facade.click("#row"+Integer.toString(count-1) + " a");
	}
	
	public JournalFormPage getNewFormPage() {
		clickNewSurveyButton();
		facade.waitForHeader(JournalFormPage.NEW_HEADER);
		return new JournalFormPage(facade);
	}
	
	public JournalFormPage getViewFormPageForMostRecent() {
		clickLastRow();
		facade.waitForHeader(JournalFormPage.EDIT_HEADER);
		return new JournalFormPage(facade);
	}
	
}
