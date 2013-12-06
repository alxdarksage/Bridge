package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CommunityPage extends EmbeddedSignInPage {

	public static final String TITLE = "Fanconi Anemia";

	public CommunityPage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void clickJoinButton() {
		facade.click("#joinAct");
	}
	
	public void clickEditButton() {
		facade.click("#editAct");	
	}
	
	public void clickPage(String pageName) {
		facade.findElement(By.partialLinkText(pageName)).click();
	}

	public void assertNavigationElementForPageHighlighted(String pageName) {
		WebElement listElement = facade.findElement(By.cssSelector("#user-nav li.active"));
		// Will throw an exception if this isn't true.
		listElement.findElement(By.partialLinkText(pageName));
	}
	
	public void assertJoinButtonPreset() {
		List<WebElement> elements = facade.findElements(By.id("joinAct"));
		Assert.assertTrue("Join button present", elements.size() == 1);
	}
	
	public void assertJoinButtonNotPreset() {
		List<WebElement> elements = facade.findElements(By.id("joinAct"));
		Assert.assertTrue("Join button present", elements.size() == 0);
	}
	
	public void assertEditButtonPreset() {
		List<WebElement> elements = facade.findElements(By.id("editAct"));
		Assert.assertTrue("Join button present", elements.size() == 1);
	}
	
	public void assertEditButtonNotPreset() {
		List<WebElement> elements = facade.findElements(By.id("editAct"));
		Assert.assertTrue("Join button present", elements.size() == 0);
	}

}
