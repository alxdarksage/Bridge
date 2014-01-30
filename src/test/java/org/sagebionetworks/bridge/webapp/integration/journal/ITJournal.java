package org.sagebionetworks.bridge.webapp.integration.journal;

import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.TrackerIndexPage;
import org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage;
import org.sagebionetworks.bridge.webapp.integration.pages.JournalPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITJournal extends WebDriverBase {

	private static final String TEST_DATE = "2013-11-03";
	private WebDriverFacade driver;
	private JournalPage journalPage;

	@Before
	public void createDriver() {
		driver = initDriver();
		SignInPage signInPage = driver.getSignInPage();
		signInPage.signInAsJoeTest();
		journalPage = driver.getJournalPage();
	}
	
	@Test
	public void canCancel() {
		TrackerIndexPage indexPage = journalPage.getTrackerIndexPage();
		
		TrackerEditPage page = indexPage.clickNewTrackerButton();
		page.clickCancelButton();
		
		journalPage.waitForTrackerIndexPage();
	}
	
	@Test
	public void redirectedToSignInPageWhenNotSignedIn() {
		driver.getSignOutPage();
		driver.getJournalPage();
		SignInPage signInPage = driver.waitForSignInPage();
		signInPage.signInAsJoeTest();
		driver.waitForJournalHomePage();
	}
	
	@Test
	public void hasCorrectContentTitle() {
		TrackerIndexPage indexPage = journalPage.getTrackerIndexPage();
		indexPage.assertCorrectHeader();
	}
	
	@Test
	public void trackerCanBeCreatedAndUpdated() {
		createTrackerSurvey();
		TrackerIndexPage indexPage = journalPage.getTrackerIndexPage();
		TrackerEditPage editPage = indexPage.getMostRecentEntry().clickEditTrackerButton();
		editPage.assertTestDate(TEST_DATE);
		editPage.assertDefaultedValuesExplanationNotPresent();
		editPage.assertRow(RBC, 1);
		editPage.assertRow(HB, 2);
		editPage.assertRow(HCT, 3);
		editPage.assertRow(MCV, 4);
		//editPage.assertRow(MCH, 5);
		editPage.assertRow(RDW, 6);
		editPage.assertRow(RET, 7);
		editPage.assertRow(WBC, 8);
		editPage.assertRow(NEUTROPHIL_PERC, 10);
		editPage.assertRow(NEUTROPHIL_IMMATURE_PERC, 11);
		editPage.assertRow(LYMPHOCYTES_PERC, 12);
		editPage.assertRow(MONOCYTES_PERC, 13);
		editPage.assertRow(PLT, 14);
		editPage.assertRow(MPV, 15);
		
		editPage.setRow(RBC, 3);
		editPage.setRow(HB, 4);
		editPage.setRow(HCT, 5);
		editPage.setRow(MCV, 6);
		//editPage.setRow(MCH, 7);
		editPage.setRow(RDW, 8);
		editPage.setRow(RET, 9);
		editPage.setRow(WBC, 10);
		editPage.setRow(NEUTROPHIL_PERC, 12);
		editPage.setRow(NEUTROPHIL_IMMATURE_PERC, 13);
		editPage.setRow(LYMPHOCYTES_PERC, 14);
		editPage.setRow(MONOCYTES_PERC, 15);
		editPage.setRow(PLT, 16);
		editPage.setRow(MPV, 17);
		editPage.submit();
		
		editPage = indexPage.getMostRecentEntry().clickEditTrackerButton();
		editPage.assertDefaultedValuesExplanationNotPresent();
		editPage.assertTestDate(TEST_DATE);
		editPage.assertRow(RBC, 3);
		editPage.assertRow(HB, 4);
		editPage.assertRow(HCT, 5);
		editPage.assertRow(MCV, 6);
		//editPage.assertRow(MCH, 7);
		editPage.assertRow(RDW, 8);
		editPage.assertRow(RET, 9);
		editPage.assertRow(WBC, 10);
		editPage.assertRow(NEUTROPHIL_PERC, 12);
		editPage.assertRow(NEUTROPHIL_IMMATURE_PERC, 13);
		editPage.assertRow(LYMPHOCYTES_PERC, 14);
		editPage.assertRow(MONOCYTES_PERC, 15);
		editPage.assertRow(PLT, 16);
		editPage.assertRow(MPV, 17);
	}
		
	@Test
	public void verifyValuesAreDefaultedFromPriorTracker() {
		createTrackerSurvey();
		TrackerIndexPage indexPage = journalPage.getTrackerIndexPage();
		TrackerEditPage newPage = indexPage.clickNewTrackerButton();
		
		newPage.assertDefaultedValuesExplanationPresent();
		newPage.assertRowShowsDefault(RBC, 1);
		newPage.assertRowShowsDefault(WBC, 8);
		newPage.assertRowShowsDefault(PLT, 14);
	}
	
	@Test
	public void verifyValuesAreValidatedAndConstrained() {
		// Might be better to allow strings to simulate typing different kinds of stuff in.
		// verify that values are constrained to valid values (doubles, longs, percentages)

		TrackerIndexPage indexPage = journalPage.getTrackerIndexPage();
		TrackerEditPage newPage = indexPage.clickNewTrackerButton();
		
		newPage.assertFieldConstrained(RBC, "asdf-10.2", "10.2");
	}
	
	/* Not anymore. They are all bound to the number field. May or may not be able to fix this.
	public void arrowKeysWork() {
		// TODO: This is a minimal test and I already know arrow keys don't work across the row
		// when there's a select control, at least in firefox.
		createNewSurvey();
		TrackerIndexPage indexPage = journalPage.getFormIndexPage();
		indexPage.getMostRecentEntry();

		WebElement first = driver.findElement(By.id("rbc"));
		first.sendKeys(Keys.ARROW_DOWN);
		WebElement currentElement = driver.switchTo().activeElement();
		Assert.assertEquals("Has moved down one row", "3", currentElement.getAttribute("value"));
		
		currentElement.sendKeys(Keys.ARROW_UP);
		currentElement = driver.switchTo().activeElement();
		Assert.assertEquals("Has moved up one row", "2", currentElement.getAttribute("value"));
	}
	*/

	private void createTrackerSurvey() {
		TrackerIndexPage indexPage = journalPage.getTrackerIndexPage();
		TrackerEditPage newPage = indexPage.clickNewTrackerButton();
		
		newPage.setTestDate(TEST_DATE);
		newPage.setRow(RBC, 1);
		newPage.setRow(HB, 2);
		newPage.setRow(HCT, 3);
		newPage.setRow(MCV, 4);
		//newPage.setRow(MCH, 5);
		newPage.setRow(RDW, 6);
		newPage.setRow(RET, 7);
		newPage.setRow(WBC, 8);
		newPage.setRow(NEUTROPHIL_PERC, 10);
		newPage.setRow(NEUTROPHIL_IMMATURE_PERC, 11);
		newPage.setRow(LYMPHOCYTES_PERC, 12);
		newPage.setRow(MONOCYTES_PERC, 13);
		newPage.setRow(PLT, 14);
		newPage.setRow(MPV, 15);
		newPage.submit();
	}
	
	public void fieldsAreValidated() {
		// TODO
	}
	
	@Test
	public void canDeleteTrackerRows() {
		TrackerIndexPage indexPage = journalPage.getTrackerIndexPage();
		int rowCount = indexPage.getDataTable().getRowCount();
		
		indexPage.getDataTable().selectRow(0);
		// indexPage.getDataTable().selectRow("October 23, 2013");
		indexPage.getDataTable().clickDelete();
		
		indexPage = journalPage.getTrackerIndexPage();
		int nextRowCount = indexPage.getDataTable().getRowCount();
		Assert.assertEquals(nextRowCount+1, rowCount);
	}
}
