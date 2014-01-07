package org.sagebionetworks.bridge.webapp.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class JournalPage {

	public static final String TITLE = "Journal";
	public static final String URL = "/journal.html";
	
	protected WebDriverFacade facade;

	public JournalPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void clickCompleteBloodCount() {
		WebElement element = facade.findElement(By.partialLinkText(FormIndexPage.HEADER));
		System.out.println(element);
		element.click();
	}
	
	public FormIndexPage getFormIndexPage() {
		clickCompleteBloodCount();
		return waitForFormIndexPage();
	}
	
	public FormIndexPage waitForFormIndexPage() {
		facade.waitForHeader(FormIndexPage.HEADER);
		return new FormIndexPage(facade);
	}
}
