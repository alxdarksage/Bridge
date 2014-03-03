package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The shim we're currently using to get HTML5 form behavior, will sometimes hide fields
 * and replace them with simulated controls. In this case the original fields are hidden
 * and can't be accessed via WebDriver, so there is more than the usual amount of JavaScript
 * in this test to work around that (accessing the hidden fields directly).
 *
 */
public class TrackerEditPage {
	
	private static final Logger logger = LogManager.getLogger(TrackerEditPage.class.getName());

	public enum FieldNames {
		RBC("M/uL"),
		HB("dL"),
		HCT("%"),
		MCV("fL"),
		//MCH("pg"),
		RDW("%"),
		RET("%"),
		WBC("K/uL"),
		NEUTROPHIL_PERC("%"),
		NEUTROPHIL_IMMATURE_PERC("%"),
		LYMPHOCYTES_PERC("%"),
		MONOCYTES_PERC("%"),
		PLT("K/uL"),
		MPV("fL");
		
		private FieldNames(String unit) { this.unit = unit; }
		private String unit;
		public String getUnit() { return unit; }
	}
	
	public static final String NEW_HEADER = "New Complete Blood Count";
	public static final String EDIT_HEADER = "Complete Blood Count";
	
	protected WebDriverFacade facade;

	public TrackerEditPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void clickSaveButton() {
		facade.click("#saveAct");
	}
	
	public void clickFinishButton() {
		facade.click("#finishAct");
	}
	
	public void clickSaveForLaterButton() {
		facade.click("#saveAct");
	}
	
	public void clickCancelButton() {
		facade.click("#cancelAct");
	}
	
	public void setRow(FieldNames field, int base) {
		setValue(field, Integer.toString(base+1));
		try {
			setUnits(field, field.getUnit());	
		} catch(Exception e) {
			logger.info(e);
			// This happens when the field is hidden, but we verify that
			// the defaulted value is correct when we assert, so this is okay
		}
		setLowRange(field, Integer.toString(base+2));
		setHighRange(field, Integer.toString(base+3));
	}
	
	public void setTestDate(String value) {
		facade.executeJavaScript("document.querySelector('#collected_on').value = '"+value+"'");
	}
	public void assertTestDate(String expectedValue) {
		String valueInForm = facade.executeJavaScriptForString("return document.querySelector('#collected_on').value");
		Assert.assertEquals("Correct value", expectedValue, valueInForm);
	}
	private void setValue(FieldNames field, String value) {
		facade.executeJavaScript("document.querySelector('#"+field.name().toLowerCase()+"-entered').value = '"+value+"'");
	}
	private void setUnits(FieldNames field, String value) {
		facade.executeJavaScript("document.querySelector('#"+field.name().toLowerCase()+"-units').value = '"+value+"'");
	}
	private void setLowRange(FieldNames field, String value) {
		facade.executeJavaScript("document.querySelector('#"+field.name().toLowerCase()+"-normalizedMin').value = '"+value+"'");
	}
	private void setHighRange(FieldNames field, String value) {
		facade.executeJavaScript("document.querySelector('#"+field.name().toLowerCase()+"-normalizedMax').value = '"+value+"'");
	}
	
	public void assertFieldConstrained(FieldNames field, String value, String expected) {
		// You can't set the field directly to bypass the fact that it is hidden, because this generates no 
		// key events and the key events are being constrained. Here you must force the element to be 
		// visible so WebDriver can interact with it.
		facade.executeJavaScript("document.querySelector('#"+field.name().toLowerCase()+"-entered').setAttribute('style','display:block!important;visibility:visible!important')");
		facade.enterField("#"+field.name().toLowerCase()+"-entered", value);
		String actual = facade.getFieldValue("#"+field.name().toLowerCase()+"-entered");
		Assert.assertEquals("Value constrained", expected, actual);
	}
	
	public void assertDefaultedValuesExplanationPresent() {
		facade.waitUntil(".aboutDefaults");
	}
	
	public void assertDefaultedValuesExplanationNotPresent() {
		List<WebElement> elements = facade.findElements(By.cssSelector(".aboutDefaults"));
		Assert.assertTrue("No explanation of default values", elements.isEmpty());
	}
	
	public void assertRow(FieldNames field, int base) {
		assertValue(field, Integer.toString(base+1));
		assertUnits(field, field.getUnit());
		assertLowRange(field, Integer.toString(base+2));
		assertHighRange(field, Integer.toString(base+3));
	}
	
	public void assertRowShowsDefault(FieldNames field, int base) {
		String cssSelector = "#"+field.name().toLowerCase();
		if (field.getUnit() != null) {
			facade.assertCssClass(cssSelector+"-units", "defaulted");
			assertUnits(field, field.getUnit());
			facade.assertCssClass(cssSelector+"-normalizedMin", "defaulted");
			assertLowRange(field, Integer.toString(base+2));
			facade.assertCssClass(cssSelector+"-normalizedMax", "defaulted");
			assertHighRange(field, Integer.toString(base+3));
		}
	}
	
	private void assertValue(FieldNames field, String value) {
		facade.assertFieldValue("#"+field.name().toLowerCase()+"-entered", value);
	}
	private void assertUnits(FieldNames field, String value) {
		facade.assertFieldValue("#"+field.name().toLowerCase()+"-units", value);
	}
	private void assertLowRange(FieldNames field, String value) {
		facade.assertFieldValue("#"+field.name().toLowerCase()+"-normalizedMin", value);
	}
	private void assertHighRange(FieldNames field, String value) {
		facade.assertFieldValue("#"+field.name().toLowerCase()+"-normalizedMax", value);
	}
}
