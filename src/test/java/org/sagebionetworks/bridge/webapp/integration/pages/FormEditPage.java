package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FormEditPage {
	
	private static final Logger logger = LogManager.getLogger(FormEditPage.class.getName());

	public enum FieldNames {
		RBC("M/uL"),
		HB("dL"),
		HCT("%"),
		MCV("fL"),
		MCH("pg"),
		RDW("%"),
		RET("%"),
		WBC("K/uL"),
		WBC_DIFF("%"),
		NEUTROPHIL("%"),
		NEUTROPHIL_IMMATURE("%"),
		LYMPHOCYTES("%"),
		MONOCYTES("%"),
		PLT("K/uL"),
		MPV("fL"),
		PDW("%");
		
		private FieldNames(String unit) { this.unit = unit; }
		private String unit;
		public String getUnit() { return unit; }
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
		facade.enterField("#testedOn", value);
	}
	public void assertTestDate(String value) {
		facade.assertFieldValue("#testedOn", value);
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
	
	public void assertRow(FieldNames field, int base) {
		assertValue(field, Integer.toString(base+1));
		assertUnits(field, field.getUnit());
		assertLowRange(field, Integer.toString(base+2));
		assertHighRange(field, Integer.toString(base+3));
	}
	
	public void assertRowShowsDefault(FieldNames field, int base) {
		String cssSelector = "#"+field.name().toLowerCase();
		if (field.getUnit() != null) {
			facade.assertCssClass(cssSelector+"_units", "defaulted");
			assertUnits(field, field.getUnit());
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
