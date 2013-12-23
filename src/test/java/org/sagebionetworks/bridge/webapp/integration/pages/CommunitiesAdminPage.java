package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CommunitiesAdminPage extends AdminPages {
	
	public static final String TITLE = "Communities Administration";
	public static final String URL = "/admin/communities/index.html";
	
	public CommunitiesAdminPage(WebDriverFacade facade) {
		super(facade);
	}
	
	public void clickNewCommunity() {
		facade.click("#newCommunityAct");
	}
	
	public WebElement getRowByName(String name) {
		WebElement table = facade.findElement(By.className("table-selectable")); 
		List<WebElement> list = table.findElements(By.partialLinkText(name));
		if (!list.isEmpty()) {
			return list.get(0).findElement(By.xpath("./ancestor::tr"));
		}
		return null;
	}
	
	public boolean rowExists(String name) {
		return (getRowByName(name) != null);
	}
	
	public void selectRow(String name) {
		WebElement parent = getRowByName(name);
		parent.findElement(By.cssSelector("input[type=checkbox]")).click();
	}
	
	public void assertRowSelected(String name) {
		WebElement parent = getRowByName(name);
		WebElement input = parent.findElement(By.cssSelector("input[type=checkbox]"));
		Assert.assertTrue(input.isSelected());
	}
	
	public void assertAllRowsSelected() {
		List<WebElement> allCheckboxes = facade.findElements(By.cssSelector("tbody tr input"));
		for (WebElement checkbox : allCheckboxes) {
			Assert.assertTrue(checkbox.isSelected());
		}
	}
	
	public void assertAllRowsDeselected() {
		List<WebElement> allCheckboxes = facade.findElements(By.cssSelector("tbody tr input"));
		for (WebElement checkbox : allCheckboxes) {
			Assert.assertFalse(checkbox.isSelected());
		}
	}
	
	public void assertDeleteEnabled() {
		String classes = facade.findElement(By.id("deleteAct")).getAttribute("class");
		Assert.assertFalse(classes.contains("disabled"));
	}
	
	public void assertDeleteDisabled() {
		String classes = facade.findElement(By.id("deleteAct")).getAttribute("class");
		Assert.assertTrue(classes.contains("disabled"));
	}
	
	public void navigateRow(int row) {
		facade.click("#n"+Integer.toString(row) + " a");
	}
	
	public void navigateRow(String name) {
		WebElement parent = getRowByName(name);
		facade.click("#"+parent.getAttribute("id") + " a");
	}
	
	public void clickMasterCheckbox() {
		facade.click("*[name=masterSelect]");
	}
	
	public void clickDelete() {
		facade.clickAndDismissConfirmation("#deleteAct");
	}

}
