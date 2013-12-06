package org.sagebionetworks.bridge.webapp.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CommunityAdminPage extends AdminPages {
	
	public static final String TITLE = "Community Administration"; 
	
	public CommunityAdminPage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void setName(String value) {
		facade.enterField("#name", value);
	}
	
	public void setDescription(String value) {
		facade.enterField("#description", value);
	}
	
	public void toggleAdminCheckbox(String name) {
		WebElement element = findCheckbox(name);
		element.click();
	}
	
	private WebElement findCheckbox(String name) {
		WebElement element = facade.findElement(By.cssSelector("div[title='"+name+"'] input"));
		if (element == null) {
			throw new RuntimeException("Could not find checkbox for community: " + name);
		}
		return element;
	}
	
	public void assertNameError() {
		facade.assertErrorMessage("#name_errors", "A name is required");
	}
	
	public void submit() {
		facade.submit("#communityForm");
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}

}
