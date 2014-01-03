package org.sagebionetworks.bridge.webapp.integration.journal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.AdminParticipantDataDescriptorsPage;
import org.sagebionetworks.bridge.webapp.integration.pages.JournalFormHistoryPage;
import org.sagebionetworks.bridge.webapp.integration.pages.JournalFormPage;
import org.sagebionetworks.bridge.webapp.integration.pages.JournalFormPage.FieldNames;
import org.sagebionetworks.bridge.webapp.integration.pages.JournalHomePage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITJournal extends WebDriverBase {

	private WebDriverFacade driver;
	private JournalHomePage homePage;

	@Before
	public void createDriver() {
		driver = initDriver();
		
		// Create that CBC record if it doesn't exist.
		driver.getAdminParticipantDataDescriptorsPage();
		SignInPage signInPage = driver.waitForSignInPage();
		signInPage.signInAsJoeTest();
		
		AdminParticipantDataDescriptorsPage page = driver.waitForAdminParticipantDataDescriptorsPage();
		page.clickCreateCBCDescriptor();
		
		homePage = driver.getJournalHomePage();
	}
	
	@Test
	public void redirectedToSignInPageWhenNotSignedIn() {
		driver.getSignOutPage();
		driver.getJournalHomePage();
		SignInPage signInPage = driver.waitForSignInPage();
		signInPage.signInAsJoeTest();
		driver.waitForJournalHomePage();
	}
	
	@Test
	public void hasCorrectContentTitle() {
		JournalFormHistoryPage historyPage = homePage.getJournalFormHistoryPage();
		historyPage.assertCorrectHeader();
	}
	
	@Test
	public void canCreateForm() {
		// TODO: Testwise, we can't undo this, which is an issue
		JournalFormHistoryPage historyPage = homePage.getJournalFormHistoryPage();
		JournalFormPage newPage = historyPage.getNewFormPage();
		
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
		
		historyPage = homePage.getJournalFormHistoryPage();
		JournalFormPage page = historyPage.getViewFormPageForMostRecent();
		page.assertRow(FieldNames.RBC, 1, "cells/mcL");
		page.assertRow(FieldNames.HB, 2, "mmol/L");
		page.assertRow(FieldNames.HCT, 3, "%");
		page.assertRow(FieldNames.MCV, 4, "fL");
		page.assertRow(FieldNames.MCH, 5, "pg");
		page.assertRow(FieldNames.RDW, 6, "s.d.");
		page.assertRow(FieldNames.RET, 7, "s.d.");
		page.assertRow(FieldNames.WBC, 8, "cells/mcL");
		page.assertRow(FieldNames.WBC_DIFF, 9, "%");
		page.assertRow(FieldNames.NEUTROPHIL, 10, "%");
		page.assertRow(FieldNames.NEUTROPHIL_IMMATURE, 11, "%");
		page.assertRow(FieldNames.LYMPHOCYTES, 12, "%");
		page.assertRow(FieldNames.MONOCYTES, 13, "%");
		page.assertRow(FieldNames.PLT, 14, "cells/mcL");
		page.assertRow(FieldNames.MPV, 15, "fL");
		page.assertRow(FieldNames.PDW, 16, "%");
	}
	
	@Test
	public void surveyCanBeUpdated() {
		// TODO
	}
		
	@Test
	public void arrowKeysWork() {
		// TODO: This is a minimal test and I already know arrow keys don't work across the row
		// when there's a select control, at least in firefox.
		JournalFormHistoryPage historyPage = homePage.getJournalFormHistoryPage();
		historyPage.getViewFormPageForMostRecent();

		WebElement first = driver.findElement(By.id("rbc"));
		first.sendKeys(Keys.ARROW_DOWN);
		WebElement currentElement = driver.switchTo().activeElement();
		Assert.assertEquals("Has moved down one row", currentElement.getAttribute("value"), "201");
		
		currentElement.sendKeys(Keys.ARROW_UP);
		currentElement = driver.switchTo().activeElement();
		Assert.assertEquals("Has moved up one row", currentElement.getAttribute("value"), "101");
	}
}
