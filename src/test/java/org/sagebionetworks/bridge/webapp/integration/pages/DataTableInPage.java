package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DataTableInPage {

	private WebDriverFacade facade;
	
	public DataTableInPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public WebElement getRowByName(String name) {
		WebElement table = facade.findElement(By.className("table-selectable")); 
		List<WebElement> list = table.findElements(By.partialLinkText(name));
		if (!list.isEmpty()) {
			return list.get(0).findElement(By.xpath("./ancestor::tr"));
		}
		return null;
	}
	
	private List<WebElement> getAllRows() {
		facade.waitUntil(".table-selectable");
		return facade.findElements(By.cssSelector(".table-selectable tbody.dataRows tr"));
	}
	
	public WebElement getRowByIndex(int index) {
		return getAllRows().get(index);
	}
	
	public void clickFirstRow() {
		List<WebElement> list = getAllRows();
		list.get(0).findElement(By.cssSelector("a")).click();
	}
	
	public void clickLastRow() {
		List<WebElement> list = getAllRows();
		list.get(list.size()-1).findElement(By.cssSelector("a")).click();
	}
	
	public int getRowCount() {
		return getAllRows().size();
	}
	
	public boolean rowExists(String name) {
		return (getRowByName(name) != null);
	}
	
	public void selectRow(String name) {
		WebElement parent = getRowByName(name);
		parent.findElement(By.cssSelector("input[type=checkbox]")).click();
	}
	
	public void selectRow(int index) {
		WebElement parent = getRowByIndex(index);
		parent.findElement(By.cssSelector("input[type=checkbox]")).click();
	}
	
	public void clickRow(String name) {
		WebElement parent = getRowByName(name);
		parent.findElement(By.cssSelector("a")).click();
	}
	
	public void assertRowSelected(String name) {
		WebElement parent = getRowByName(name);
		WebElement input = parent.findElement(By.cssSelector("input[type=checkbox]"));
		Assert.assertTrue(input.isSelected());
	}
	
	public void assertAllRowsSelected() {
		List<WebElement> allCheckboxes = facade.findElements(By.cssSelector("tbody.dataRows input"));
		for (WebElement checkbox : allCheckboxes) {
			Assert.assertTrue(checkbox.isSelected());
		}
	}
	
	public void assertAllRowsDeselected() {
		List<WebElement> allCheckboxes = facade.findElements(By.cssSelector("tbody.dataRows input"));
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
	
	public void clickMasterCheckbox() {
		facade.click("*[name=masterSelect]");
	}
	
	public void clickDelete() {
		facade.clickAndDismissConfirmation("#deleteAct");
	}
}
