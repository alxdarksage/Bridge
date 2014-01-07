package org.sagebionetworks.bridge.webapp.integration.journal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.AdminParticipantDataDescriptorsPage;
import org.sagebionetworks.bridge.webapp.integration.pages.FormIndexPage;
import org.sagebionetworks.bridge.webapp.integration.pages.FormEditPage;
import org.sagebionetworks.bridge.webapp.integration.pages.FormEditPage.FieldNames;
import org.sagebionetworks.bridge.webapp.integration.pages.JournalPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITJournal extends WebDriverBase {

	private WebDriverFacade driver;
	private JournalPage journalPage;

	@Before
	public void createDriver() {
		driver = initDriver();
		
		// Create descriptor records if they don't exist.
		driver.getAdminParticipantDataDescriptorsPage();
		SignInPage signInPage = driver.waitForSignInPage();
		signInPage.signInAsJoeTest();
		
		AdminParticipantDataDescriptorsPage page = driver.waitForAdminParticipantDataDescriptorsPage();
		page.clickCreateDescriptors();
		
		journalPage = driver.getJournalPage();
	}
	
	@Test
	public void canCancel() {
		FormIndexPage indexPage = journalPage.getFormIndexPage();
		
		FormEditPage page = indexPage.clickNewSurveyButton();
		page.clickCancelButton();
		
		journalPage.waitForFormIndexPage();
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
		FormIndexPage indexPage = journalPage.getFormIndexPage();
		indexPage.assertCorrectHeader();
	}
	
	@Test
	public void surveyCanBeCreatedAndUpdated() {
		createNewSurvey();
		FormIndexPage indexPage = journalPage.getFormIndexPage();
		FormEditPage editPage = indexPage.getMostRecentEntry();
		
		editPage.assertDefaultedValuesExplanationNotPresent();
		editPage.assertRow(FieldNames.RBC, 1, "cells/mcL");
		editPage.assertRow(FieldNames.HB, 2, "mmol/L");
		editPage.assertRow(FieldNames.HCT, 3, "%");
		editPage.assertRow(FieldNames.MCV, 4, "fL");
		editPage.assertRow(FieldNames.MCH, 5, "pg");
		editPage.assertRow(FieldNames.RDW, 6, "s.d.");
		editPage.assertRow(FieldNames.RET, 7, "s.d.");
		editPage.assertRow(FieldNames.WBC, 8, "cells/mcL");
		editPage.assertRow(FieldNames.WBC_DIFF, 9, "%");
		editPage.assertRow(FieldNames.NEUTROPHIL, 10, "%");
		editPage.assertRow(FieldNames.NEUTROPHIL_IMMATURE, 11, "%");
		editPage.assertRow(FieldNames.LYMPHOCYTES, 12, "%");
		editPage.assertRow(FieldNames.MONOCYTES, 13, "%");
		editPage.assertRow(FieldNames.PLT, 14, "cells/mcL");
		editPage.assertRow(FieldNames.MPV, 15, "fL");
		editPage.assertRow(FieldNames.PDW, 16, "%");		
		
		editPage.setRow(FieldNames.RBC, 3, "cells/mcL");
		editPage.setRow(FieldNames.HB, 4, "mmol/L");
		editPage.setRow(FieldNames.HCT, 5, null);
		editPage.setRow(FieldNames.MCV, 6, null);
		editPage.setRow(FieldNames.MCH, 7, null);
		editPage.setRow(FieldNames.RDW, 8, "s.d.");
		editPage.setRow(FieldNames.RET, 9, "s.d.");
		editPage.setRow(FieldNames.WBC, 10, "cells/mcL");
		editPage.setRow(FieldNames.WBC_DIFF, 11, null);
		editPage.setRow(FieldNames.NEUTROPHIL, 12, null);
		editPage.setRow(FieldNames.NEUTROPHIL_IMMATURE, 13, null);
		editPage.setRow(FieldNames.LYMPHOCYTES, 14, null);
		editPage.setRow(FieldNames.MONOCYTES, 15, null);
		editPage.setRow(FieldNames.PLT, 16, "cells/mcL");
		editPage.setRow(FieldNames.MPV, 17, null);
		editPage.setRow(FieldNames.PDW, 18, null);
		editPage.submit();
		
		editPage = indexPage.getMostRecentEntry();
		editPage.assertDefaultedValuesExplanationNotPresent();
		editPage.assertRow(FieldNames.RBC, 3, "cells/mcL");
		editPage.assertRow(FieldNames.HB, 4, "mmol/L");
		editPage.assertRow(FieldNames.HCT, 5, "%");
		editPage.assertRow(FieldNames.MCV, 6, "fL");
		editPage.assertRow(FieldNames.MCH, 7, "pg");
		editPage.assertRow(FieldNames.RDW, 8, "s.d.");
		editPage.assertRow(FieldNames.RET, 9, "s.d.");
		editPage.assertRow(FieldNames.WBC, 10, "cells/mcL");
		editPage.assertRow(FieldNames.WBC_DIFF, 11, "%");
		editPage.assertRow(FieldNames.NEUTROPHIL, 12, "%");
		editPage.assertRow(FieldNames.NEUTROPHIL_IMMATURE, 13, "%");
		editPage.assertRow(FieldNames.LYMPHOCYTES, 14, "%");
		editPage.assertRow(FieldNames.MONOCYTES, 15, "%");
		editPage.assertRow(FieldNames.PLT, 16, "cells/mcL");
		editPage.assertRow(FieldNames.MPV, 17, "fL");
		editPage.assertRow(FieldNames.PDW, 18, "%");		
	}
		
	@Test
	public void verifyValuesAreDefaultedFromPriorSurvey() {
		createNewSurvey();
		FormIndexPage indexPage = journalPage.getFormIndexPage();
		FormEditPage newPage = indexPage.clickNewSurveyButton();
		
		newPage.assertDefaultedValuesExplanationPresent();
		newPage.assertRowShowsDefault(FieldNames.RBC, 1, "cells/mcL");
		newPage.assertRowShowsDefault(FieldNames.HB, 2, "mmol/L");
		newPage.assertRowShowsDefault(FieldNames.HCT, 3, null);
		newPage.assertRowShowsDefault(FieldNames.MCV, 4, null);
		newPage.assertRowShowsDefault(FieldNames.MCH, 5, null);
		newPage.assertRowShowsDefault(FieldNames.RDW, 6, "s.d.");
		newPage.assertRowShowsDefault(FieldNames.RET, 7, "s.d.");
		newPage.assertRowShowsDefault(FieldNames.WBC, 8, "cells/mcL");
		newPage.assertRowShowsDefault(FieldNames.WBC_DIFF, 9, null);
		newPage.assertRowShowsDefault(FieldNames.NEUTROPHIL, 10, null);
		newPage.assertRowShowsDefault(FieldNames.NEUTROPHIL_IMMATURE, 11, null);
		newPage.assertRowShowsDefault(FieldNames.LYMPHOCYTES, 12, null);
		newPage.assertRowShowsDefault(FieldNames.MONOCYTES, 13, null);
		newPage.assertRowShowsDefault(FieldNames.PLT, 14, "cells/mcL");
		newPage.assertRowShowsDefault(FieldNames.MPV, 15, null);
		newPage.assertRowShowsDefault(FieldNames.PDW, 16, null);
	}
	
	@Test
	public void verifyValuesAreValidatedAndConstrained() {
		// Might be better to allow strings to simulate typing different kinds of stuff in.
		// verify that values are constrained to valid values (doubles, longs, percentages)

		FormIndexPage indexPage = journalPage.getFormIndexPage();
		FormEditPage newPage = indexPage.clickNewSurveyButton();
		
		newPage.assertFieldConstrained(FieldNames.RBC, "asdf-10.2", "10.2");
	}
	
	@Test
	public void arrowKeysWork() {
		// TODO: This is a minimal test and I already know arrow keys don't work across the row
		// when there's a select control, at least in firefox.
		FormIndexPage indexPage = journalPage.getFormIndexPage();
		indexPage.getMostRecentEntry();

		WebElement first = driver.findElement(By.id("rbc"));
		first.sendKeys(Keys.ARROW_DOWN);
		WebElement currentElement = driver.switchTo().activeElement();
		Assert.assertEquals("Has moved down one row", currentElement.getAttribute("value"), "201");
		
		currentElement.sendKeys(Keys.ARROW_UP);
		currentElement = driver.switchTo().activeElement();
		Assert.assertEquals("Has moved up one row", currentElement.getAttribute("value"), "101");
	}

	private void createNewSurvey() {
		FormIndexPage indexPage = journalPage.getFormIndexPage();
		FormEditPage newPage = indexPage.clickNewSurveyButton();
		
		newPage.setRow(FieldNames.RBC, 1, "cells/mcL");
		newPage.setRow(FieldNames.HB, 2, "mmol/L");
		newPage.setRow(FieldNames.HCT, 3, null);
		newPage.setRow(FieldNames.MCV, 4, null);
		newPage.setRow(FieldNames.MCH, 5, null);
		newPage.setRow(FieldNames.RDW, 6, "s.d.");
		newPage.setRow(FieldNames.RET, 7, "s.d.");
		newPage.setRow(FieldNames.WBC, 8, "cells/mcL");
		newPage.setRow(FieldNames.WBC_DIFF, 9, null);
		newPage.setRow(FieldNames.NEUTROPHIL, 10, null);
		newPage.setRow(FieldNames.NEUTROPHIL_IMMATURE, 11, null);
		newPage.setRow(FieldNames.LYMPHOCYTES, 12, null);
		newPage.setRow(FieldNames.MONOCYTES, 13, null);
		newPage.setRow(FieldNames.PLT, 14, "cells/mcL");
		newPage.setRow(FieldNames.MPV, 15, null);
		newPage.setRow(FieldNames.PDW, 16, null);
		newPage.submit();
	}
	
}
