package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.webapp.converter.DateToISODateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToShortFormatDateStringConverter;

/**
 * This is used to model the everything-in-one-page, FormLayout.ALL_RECORDS_ONE_PAGE_INLINE pages.
 * There's only one, so this is hard-wired to deal with the split tables on the medications tracker
 * (two data-table like tables).
 */
public class OnePageTrackerEditPage {

	private static final Logger logger = LogManager.getLogger(OnePageTrackerEditPage.class.getName());
	
	public static final String HEADER = "Medication Tracker";
	
	protected WebDriverFacade facade;
	protected DataTableInPage activeTable;
	protected DataTableInPage finishedTable;

	public OnePageTrackerEditPage(WebDriverFacade facade) {
		this.facade = facade;
		this.activeTable = new DataTableInPage(facade, "#activeTable");
		this.finishedTable = new DataTableInPage(facade, "#finishedTable");
	}
	
	public void enterMedication(String medication, String dose, String doseInfo, Date startDate, Date endDate) {
		facade.enterField("#medication", (medication != null) ? medication : "");	
		facade.enterField("#dose", (dose != null) ? dose : "");	
		facade.enterField("#dose_instructions", (doseInfo != null) ? doseInfo : "");	
		if (startDate != null) {
			setDate("#start_date", startDate);
		}
		if (endDate != null) {
			setDate("#end_date", endDate);
		}
		facade.waitFor(1000L);
	}
	
	public void assertMedication(String medication, String dose, String doseInfo, Date startDate, Date endDate) {
		DateToISODateStringConverter converter = new DateToISODateStringConverter();
		ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
		
		if (medication != null) {
			facade.assertFieldValue("#medication", medication);	
		}
		if (dose != null) {
			facade.assertFieldValue("#dose", dose);	
		}
		if (doseInfo != null) {
			facade.assertFieldValue("#dose_instructions", doseInfo);	
		}
		if (startDate != null) {
			pdv.setValue(startDate.getTime());
			facade.assertFieldValue("#start_date", converter.convert(pdv).get(0));
		}
		if (endDate != null) {
			pdv.setValue(endDate.getTime());
			facade.assertFieldValue("#end_date", converter.convert(pdv).get(0));
		}
	}
	
	public void setDate(String cssSelector, Date date) {
		DateToISODateStringConverter converter = new DateToISODateStringConverter();
		ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
		pdv.setValue(date.getTime());
		String value = converter.convert(pdv).get(0);
		
		String hiddenDateFormSelector = cssSelector + " + input";
		
		facade.executeJavaScript("document.querySelector('"+hiddenDateFormSelector+"').value = '"+value+"';");
		facade.executeJavaScript("document.querySelector('"+cssSelector+"').value = '"+value+"';");
	}
	
	public void waitUntilRecordIsClosed() {
		facade.waitUntil("#finishedTable tbody.dataRows tr:first-child");
	}
	
	public void clickSaveButton() {
		facade.click("#saveAct");
	}
	
	public DataTableInPage getActiveMedications() {
		return activeTable;
	}
	
	public DataTableInPage getFinishedMedications() {
		return finishedTable;
	}
	
	public void assertColumnValue(String cssSelector, Date date) {
		DateToShortFormatDateStringConverter converter = new DateToShortFormatDateStringConverter();
		ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
		pdv.setValue(date.getTime());
		String value = converter.convert(pdv).get(0);
		
		WebElement element = facade.findElement(By.cssSelector(cssSelector));
		Assert.assertEquals(value, element.getText());
	}
	
}
