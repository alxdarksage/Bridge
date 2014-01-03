package org.sagebionetworks.bridge.webapp.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class JournalHomePage {

	public static final String TITLE = "Journal";
	public static final String URL = "/journal.html";
	
	protected WebDriverFacade facade;

	public JournalHomePage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void clickCompleteBloodCount() {
		WebElement element = facade.findElement(By.partialLinkText(JournalFormHistoryPage.HEADER));
		System.out.println(element);
		element.click();
	}
	
	public JournalFormHistoryPage getJournalFormHistoryPage() {
		clickCompleteBloodCount();
		facade.waitForHeader(JournalFormHistoryPage.HEADER);
		return new JournalFormHistoryPage(facade);
	}
	
}
