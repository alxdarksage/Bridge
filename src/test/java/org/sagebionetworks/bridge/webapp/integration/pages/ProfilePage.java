package org.sagebionetworks.bridge.webapp.integration.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.web.util.HtmlUtils;


public class ProfilePage extends EmbeddedSignInPage {

	public static final String TITLE = "Profile";
	public static final String URL = "/profile.html";
	
	public ProfilePage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}
	
	public void assertCommunitySelected(String communityName) {
		WebElement element = facade.findCheckbox(communityName);
		Assert.assertTrue("Community " + communityName + " is checked", element.isSelected());
	}
	
	public void assertCommunityNotSelected(String communityName) {
		WebElement element = facade.findCheckbox(communityName);
		Assert.assertFalse("Community " + communityName + " not checked", element.isSelected());
	}
	
	public void toggleCommunityCheckbox(String communityName) {
		WebElement element = facade.findCheckbox(communityName);
		element.click();
	}
	
	public void assertNeedAtLeastOneAdminError() {
		facade.assertErrorMessage("#memberships_errors", "Need at least one admin.");
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
