package org.sagebionetworks.bridge.webapp.integration.pages;

public class JournalFormPage {
	
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

	public JournalFormPage(WebDriverFacade facade) {
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
			setUnits(field, units);
		}
		setLowRange(field, Integer.toString(base+2));
		setHighRange(field, Integer.toString(base+3));
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
	
	public void assertRow(FieldNames field, int base, String units) {
		base = base*100;
		assertValue(field, Integer.toString(base+1));
		if (units != null) {
			assertUnits(field, units);
		}
		assertLowRange(field, Integer.toString(base+2));
		assertHighRange(field, Integer.toString(base+3));
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
