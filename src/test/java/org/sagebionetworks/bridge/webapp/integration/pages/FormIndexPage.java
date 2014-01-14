package org.sagebionetworks.bridge.webapp.integration.pages;

import org.openqa.selenium.By;

public class FormIndexPage {
	
	public static final String HEADER = "Complete Blood Count";

	protected WebDriverFacade facade;

	public FormIndexPage(WebDriverFacade facade) {
		this.facade = facade;
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
	
	public FormEditPage clickNewSurveyButton() {
		facade.click("#newSurveyAct");
		facade.waitForHeader(FormEditPage.NEW_HEADER);
		return new FormEditPage(facade);
	}
	
	public FormShowPage getMostRecentEntry() {
		clickLastRow();
		facade.waitForHeader(FormShowPage.HEADER);
		return new FormShowPage(facade);
	}
	
}
