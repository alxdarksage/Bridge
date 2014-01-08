package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FormEditPage {
	
	public enum FieldNames {
		RBC,
		HB,
		HCT,
		MCV,
		MCH,
		RDW,
		RET,
		WBC,
		WBC_DIFF,
		NEUTROPHIL,
		NEUTROPHIL_IMMATURE,
		LYMPHOCYTES,
		MONOCYTES,
		PLT,
		MPV,
		PDW
	}
	
	public static final String NEW_HEADER = "New Complete Blood Count";
	public static final String EDIT_HEADER = "Complete Blood Count";
	
	protected WebDriverFacade facade;

	public FormEditPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void submit() {
		facade.submit("#dynamicForm");
	}
	
	public void clickCancelButton() {
		facade.click("#cancelAct");
	}
	
	public void setRow(FieldNames field, int base, String units) {
		base = base*100;
		setValue(field, Integer.toString(base+1));
		if (units != null) {
			// The only units like this right now are percentages, and the low-high range is fixed as well
			setUnits(field, units);
			setLowRange(field, Integer.toString(base+2));
			setHighRange(field, Integer.toString(base+3));
		}
	}
	
	private void setValue(FieldNames field, String value) {
		facade.enterField("#"+field.name().toLowerCase(), value);
	}
	private void setUnits(FieldNames field, String value) {
		facade.enterField("#"+field.name().toLowerCase()+"_units", value);
	}
	private void setLowRange(FieldNames field, String value) {
		facade.enterField("#"+field.name().toLowerCase()+"_range_low", value);
	}
	private void setHighRange(FieldNames field, String value) {
		facade.enterField("#"+field.name().toLowerCase()+"_range_high", value);
	}
	
	public void assertFieldConstrained(FieldNames field, String value, String expected) {
		facade.enterField("#"+field.name().toLowerCase(), value);
		String actual = facade.getFieldValue("#"+field.name().toLowerCase());
		Assert.assertEquals("Value constrained", expected, actual);
	}
	
	public void assertDefaultedValuesExplanationPresent() {
		facade.waitUntil(".aboutDefaults");
	}
	
	public void assertDefaultedValuesExplanationNotPresent() {
		List<WebElement> elements = facade.findElements(By.cssSelector(".aboutDefaults"));
		Assert.assertTrue("No explanation of default values", elements.isEmpty());
	}
	
	public void assertRow(FieldNames field, int base, String units) {
		base = base*100;
		assertValue(field, Integer.toString(base+1));
		if (units != null) {
			assertUnits(field, units);
		}
		String low = ("%".equals(units)) ? "0" : Integer.toString(base+2);
		String high = ("%".equals(units)) ? "100" : Integer.toString(base+3);
		assertLowRange(field, low);
		assertHighRange(field, high);
	}
	
	public void assertRowShowsDefault(FieldNames field, int base, String units) {
		String cssSelector = "#"+field.name().toLowerCase();
		base = base*100;
		if (units != null) {
			facade.assertCssClass(cssSelector+"_units", "defaulted");
			assertUnits(field, units);
			facade.assertCssClass(cssSelector+"_range_low", "defaulted");
			assertLowRange(field, Integer.toString(base+2));
			facade.assertCssClass(cssSelector+"_range_high", "defaulted");
			assertHighRange(field, Integer.toString(base+3));
		}
	}
	
	private void assertValue(FieldNames field, String value) {
		facade.assertFieldValue("#"+field.name().toLowerCase(), value);
	}
	private void assertUnits(FieldNames field, String value) {
		facade.assertFieldValue("#"+field.name().toLowerCase()+"_units", value);
	}
	private void assertLowRange(FieldNames field, String value) {
		facade.assertFieldValue("#"+field.name().toLowerCase()+"_range_low", value);
	}
	private void assertHighRange(FieldNames field, String value) {
		facade.assertFieldValue("#"+field.name().toLowerCase()+"_range_high", value);
	}
}
