package org.sagebionetworks.bridge.webapp.integration.journal;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.DataTableInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.OnePageTrackerEditPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITMedicationsTracker extends WebDriverBase {

	private static final Date START_DATE = new Date(1284102000000L);
	private static final Date END_DATE = new Date(1391634772472L);
	
	private WebDriverFacade driver;
	private OnePageTrackerEditPage medPage;

	@Before
	public void createDriver() {
		driver = initDriver();
		SignInPage signInPage = driver.getSignInPage();
		signInPage.signInAsJoeTest();
		medPage = driver.getJournalPage().getMedicationPage();
		
		// Get rid of this data to delete it
		medPage.enterMedication("medication", "dose", "doseInfo", START_DATE, END_DATE);
		medPage.clickSaveButton();
		
		// This tests deletion, de facto. Tests would succeed if this didn't work.
		medPage = driver.getJournalPage().getMedicationPage();
		deleteAllInTable(medPage.getActiveMedications());
		deleteAllInTable(medPage.getFinishedMedications());
	}
	
	private void deleteAllInTable(DataTableInPage table) {
		int count = table.getRowCount();
		if (count > 0) {
			for (int i=0; i < count; i++) {
				table.selectRow(i);
			}
			table.clickDelete();	
		}
	}

	@Test
	public void canPartiallyFillOutAndResumeInlineForm() throws Exception {
		medPage.enterMedication("Prinivil", null, null, START_DATE, null);
		driver.getPortalPage();

		medPage = driver.getJournalPage().getMedicationPage();
		medPage.assertMedication("Prinivil", null, null, START_DATE, null);
	}
	
	@Test
	public void inlineFormIsValidatedOnSave() throws Exception {
		medPage.enterMedication("Prinivil", null, null, START_DATE, null);
		medPage.clickSaveButton();

		WebElement element = driver.findElement(By.cssSelector("#dose"));
		Assert.assertEquals("true", element.getAttribute("aria-invalid"));

		medPage.enterMedication("Prinivil", "200mg", null, START_DATE, null);
		medPage.clickSaveButton();

		element = driver.findElement(By.cssSelector("#dose"));
		// depending on the browser, this is either false or not present at all:
		String invalid = element.getAttribute("aria-invalid");
		Assert.assertTrue(invalid == null || "false".equals(invalid));
	}
	
	@Test
	public void incompleteEntryEndsUpInActiveTable() throws Exception {
		medPage.enterMedication("Prinivil", "10mg", "1x/day", START_DATE, null);
		medPage.clickSaveButton();
		
		// It has been copied out of the inline editor
		medPage = driver.getJournalPage().getMedicationPage();
		medPage.assertMedication("", "", "", null, null);
		
		Assert.assertEquals(1, medPage.getActiveMedications().getRowCount());
		Assert.assertEquals(0, medPage.getFinishedMedications().getRowCount());
	}
	
	@Test
	public void completeEntryEndsUpInCompleteTable() throws Exception {
		medPage.enterMedication("Prinivil", "10mg", "1x/day", START_DATE, END_DATE);
		medPage.clickSaveButton();
		
		Assert.assertEquals(0, medPage.getActiveMedications().getRowCount());
		Assert.assertEquals(1, medPage.getFinishedMedications().getRowCount());
	}
	
	@Test
	public void canCompleteActiveEntry() throws Exception {
		medPage.enterMedication("Prinivil", "10mg", "1x/day", START_DATE, null);
		medPage.clickSaveButton();

		medPage = driver.getJournalPage().getMedicationPage();
		
		medPage.setDate("#activeTable tbody tr:first-child #end_date", END_DATE);
		
		medPage.waitUntilRecordIsClosed();
		Assert.assertEquals(0, medPage.getActiveMedications().getRowCount());
		Assert.assertEquals(1, medPage.getFinishedMedications().getRowCount());
	}
	
	@Test
	public void endDateAfterStartDate() {
		medPage.enterMedication("Prinivil", "10mg", "1x/day", END_DATE, START_DATE);
		medPage.clickSaveButton();

		medPage = driver.getJournalPage().getMedicationPage();
		medPage.assertColumnValue("#finishedTable tbody.dataRows tr:first-child td:nth-child(5) span", START_DATE);
		medPage.assertColumnValue("#finishedTable tbody.dataRows tr:first-child td:nth-child(6) span", START_DATE);
	}
}
