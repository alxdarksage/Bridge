package org.sagebionetworks.bridge.webapp.integration.journal;

import java.util.Date;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.MedicationPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITMedicationsTracker extends WebDriverBase {

	private static final Date START_DATE = new Date(1284102000000L);
	private static final Date START_DATE2 = new Date(1344102000000L);
	private static final Date END_DATE = new Date(1391634772472L);
	
	private WebDriverFacade driver;
	private MedicationPage medPage;

	@Before
	public void createDriver() {
		driver = initDriver();
		SignInPage signInPage = driver.getSignInPage();
		signInPage.signInAsJoeTest();
		medPage = driver.getJournalPage().getMedicationPage();
	}

	@Test
	public void canCreateNewMedication() throws Exception {
		String newMedName = "med" + new Random().nextInt();
		medPage.enterNewMedication(newMedName, "10mg", START_DATE);
		medPage.clickSubmitNewMedButton();
		medPage.waitForCurrentMedication(newMedName, "10mg", START_DATE);

		medPage.clickChangeDose(newMedName);
		medPage.enterChangeDose("20mg", START_DATE2);
		medPage.clickSubmitNewDoseButton();
		medPage.waitForCurrentMedication(newMedName, "20mg", START_DATE2);

		medPage.clickEndMed(newMedName);
		medPage.enterEndMed(END_DATE);
		medPage.clickSubmitEndMedButton();
		medPage.waitForHistoricMedication(newMedName, "20mg", START_DATE2, END_DATE);
		medPage.assertNoCurrentMedication(newMedName);
	}

	@Test
	public void cannotCreateNewMedicationWithoutName() throws Exception {
		medPage.enterNewMedication("", "10mg", START_DATE);
		medPage.clickSubmitNewMedButton();
		medPage.assertEmptyMedWarning();
	}
}
