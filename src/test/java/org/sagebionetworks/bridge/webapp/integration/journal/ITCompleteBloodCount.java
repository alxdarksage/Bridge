package org.sagebionetworks.bridge.webapp.integration.journal;

import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.HB;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.HCT;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.LYMPHOCYTES_PERC;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.MCV;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.MONOCYTES_PERC;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.MPV;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.NEUTROPHIL_IMMATURE_PERC;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.NEUTROPHIL_PERC;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.PLT;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.RBC;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.RDW;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.RET;
import static org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage.FieldNames.WBC;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.JournalPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.TrackerEditPage;
import org.sagebionetworks.bridge.webapp.integration.pages.TrackerIndexPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITCompleteBloodCount extends WebDriverBase {

	// This HAS to be the latest date in the system, with a date later 
	// than the dummy records we create for testing.
	private static final String TEST_DATE = "2013-12-25";
	private WebDriverFacade driver;
	private JournalPage journalPage;

	@Before
	public void createDriver() {
		driver = initDriver();
		SignInPage signInPage = driver.getSignInPage();
		signInPage.signInAsJoeTest();
		journalPage = driver.getJournalPage();
		
		deleteAllTrackers();
	}
	
	private void deleteAllTrackers() {
		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
		int count = indexPage.getDataTable().getRowCount();
		if (count > 0) {
			for (int i=0; i < count; i++) {
				indexPage.getDataTable().selectRow(i);
			}
			indexPage.getDataTable().clickDelete();	
		}
	}
	
	private void createTrackerSurvey() {
		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
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
		newPage.clickFinishButton();
	}

	@Test
	public void canCancel() {
		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
		
		TrackerEditPage page = indexPage.clickNewTrackerButton();
		page.clickCancelButton();
		
		journalPage.waitForCBCIndexPage();
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
		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
		indexPage.assertCorrectHeader();
	}
	
	@Test
	public void trackerCanBeCreatedAndUpdated() {
		createTrackerSurvey();
		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
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
		editPage.clickSaveButton();
		
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
		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
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

		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
		TrackerEditPage newPage = indexPage.clickNewTrackerButton();
		
		newPage.assertFieldConstrained(RBC, "asdf-10.2", "10.2");
	}
	
	@Test
	public void saveAndResumeAForm() {
		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
		indexPage.assertControlsForAllTrackersComplete();
		
		// Create a partially complete form
		TrackerEditPage newPage = indexPage.clickNewTrackerButton();
		newPage.setTestDate(TEST_DATE);
		newPage.setRow(RBC, 1);
		newPage.setRow(HB, 2);
		newPage.setRow(HCT, 3);
		newPage.clickSaveForLaterButton();
		
		indexPage = journalPage.getCBCIndexPage();
		// Controls should exist to resume
		indexPage.assertControlsForAnUnfinishedTracker();
		// And it should not be in the table
		Assert.assertEquals(0, indexPage.getDataTable().getRowCount());
		
		// Edit and save for later.
		TrackerEditPage editPage = indexPage.clickResumeTrackerButton();
		editPage.setRow(HB, 4);
		editPage.clickSaveForLaterButton();
		
		// Now resume again, edit and finish
		indexPage = journalPage.getCBCIndexPage();
		// Still not in table
		Assert.assertEquals(0, indexPage.getDataTable().getRowCount());
		
		editPage = indexPage.clickResumeTrackerButton();
		editPage.setRow(HCT, 5);
		editPage.clickFinishButton();
		
		// It's no longer in process, it's in the table
		indexPage = journalPage.getCBCIndexPage();
		indexPage.assertControlsForAllTrackersComplete();
		Assert.assertEquals(1, indexPage.getDataTable().getRowCount());
		
		editPage = indexPage.getMostRecentEntry().clickEditTrackerButton();
		editPage.setRow(RBC, 1);
		editPage.setRow(HB, 4);
		editPage.setRow(HCT, 5);
	}
	
	@Test
	public void canDeleteTrackerRows() {
		deleteAllTrackers();
		createTrackerSurvey();
		createTrackerSurvey();
		createTrackerSurvey();

		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
		int rowCount = indexPage.getDataTable().getRowCount();
		
		indexPage.getDataTable().selectRow(0);
		indexPage.getDataTable().clickDelete();
		
		indexPage = journalPage.getCBCIndexPage();
		int nextRowCount = indexPage.getDataTable().getRowCount();
		Assert.assertEquals(nextRowCount+1, rowCount);
	}
	
	@Test
	public void canExportTrackers() throws Exception {
		deleteAllTrackers();
		createTrackerSurvey();
		
		TrackerIndexPage indexPage = journalPage.getCBCIndexPage();
		indexPage.clickExportButton();
		// and then I don't know what.
	}
	
	
}
