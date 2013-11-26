package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ProfilePage extends EmbeddedSignInPage {

	public static final String TITLE = "Profile";
	
	public ProfilePage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}
	
	public void assertCommunitySelected(String communityName) {
		String id = getCommunityId();
		System.out.println("ID: " + id);
		List<WebElement> elements = facade.findElements(By.tagName(".m"+id));
		System.out.println(elements.size());
		boolean checked = (elements.size() == 1 && elements.get(0).isSelected());
		Assert.assertTrue("Community " + communityName + " checked", checked);
	}
	
	/*
	public void assertCommunityNotSelected(String communityName) {
		String id = getCommunityId();
		List<WebElement> elements = facade.findElements(By.cssSelector(".m"+id));
		boolean checked = (elements.size() == 1 && elements.get(0).isSelected());
		Assert.assertFalse("Community " + communityName + " not checked", checked);
	}
	*/
	
	private String getCommunityId() {
		Map<String,String> ids = facade.getPortalPage().getCommunityIds();
		return ids.get("Fanconi Anemia");
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
