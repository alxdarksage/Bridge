package org.sagebionetworks.bridge.webapp.integration.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.web.util.HtmlUtils;


public class ProfilePage extends EmbeddedSignInPage {

	public static final String TITLE = "Profile";
	
	public ProfilePage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}
	
	public void assertCommunitySelected(String communityName) {
		WebElement element = findCheckbox(communityName);
		Assert.assertTrue("Community " + communityName + " is checked", element.isSelected());
	}
	
	public void assertCommunityNotSelected(String communityName) {
		WebElement element = findCheckbox(communityName);
		Assert.assertFalse("Community " + communityName + " not checked", element.isSelected());
	}
	
	public void toggleCommunityCheckbox(String communityName) {
		WebElement element = findCheckbox(communityName);
		element.click();
	}
	
	public void assertNeedAtLeastOneAdminError() {
		facade.assertErrorMessage("#memberships_errors", "Need at least one admin.");
	}
	
	private WebElement findCheckbox(String communityName) {
		String name = HtmlUtils.htmlEscape(communityName);
		WebElement element = facade.findElement(By.cssSelector("div[title='"+name+" Membership'] input"));
		if (element == null) {
			throw new RuntimeException("Could not find checkbox for community: " + name);
		}
		return element;
	}
	
	public void setDisplayName(String value) {
		facade.enterField("#displayName", value);
	}
	public void setFirstName(String value) {
		facade.enterField("#firstName", value);
	}
	public void setLastName(String value) {
		facade.enterField("#lastName", value);
	}
	public void setDescription(String value) {
		facade.enterField("#summary", value);
	}
	public void submit() {
		facade.submit("#profileForm");
	}
}
