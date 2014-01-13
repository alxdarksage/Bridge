package org.sagebionetworks.bridge.webapp.integration.journal;

import static org.sagebionetworks.bridge.webapp.integration.pages.FormEditPage.FieldNames.*;

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
		editPage.assertTestDate(TEST_DATE);
		editPage.assertDefaultedValuesExplanationNotPresent();
		editPage.assertRow(RBC, 1);
		editPage.assertRow(HB, 2);
		editPage.assertRow(HCT, 3);
		editPage.assertRow(MCV, 4);
		editPage.assertRow(MCH, 5);
		editPage.assertRow(RDW, 6);
		editPage.assertRow(RET, 7);
		editPage.assertRow(WBC, 8);
		editPage.assertRow(WBC_DIFF, 9);
		editPage.assertRow(NEUTROPHIL, 10);
		editPage.assertRow(NEUTROPHIL_IMMATURE, 11);
		editPage.assertRow(LYMPHOCYTES, 12);
		editPage.assertRow(MONOCYTES, 13);
		editPage.assertRow(PLT, 14);
		editPage.assertRow(MPV, 15);
		editPage.assertRow(PDW, 16);		
		
		editPage.setRow(RBC, 3);
		editPage.setRow(HB, 4);
		editPage.setRow(HCT, 5);
		editPage.setRow(MCV, 6);
		editPage.setRow(MCH, 7);
		editPage.setRow(RDW, 8);
		editPage.setRow(RET, 9);
		editPage.setRow(WBC, 10);
		editPage.setRow(WBC_DIFF, 11);
		editPage.setRow(NEUTROPHIL, 12);
		editPage.setRow(NEUTROPHIL_IMMATURE, 13);
		editPage.setRow(LYMPHOCYTES, 14);
		editPage.setRow(MONOCYTES, 15);
		editPage.setRow(PLT, 16);
		editPage.setRow(MPV, 17);
		editPage.setRow(PDW, 18);
		editPage.submit();
		
		editPage = indexPage.getMostRecentEntry();
		editPage.assertDefaultedValuesExplanationNotPresent();
		editPage.assertTestDate(TEST_DATE);
		editPage.assertRow(RBC, 3);
		editPage.assertRow(HB, 4);
		editPage.assertRow(HCT, 5);
		editPage.assertRow(MCV, 6);
		editPage.assertRow(MCH, 7);
		editPage.assertRow(RDW, 8);
		editPage.assertRow(RET, 9);
		editPage.assertRow(WBC, 10);
		editPage.assertRow(WBC_DIFF, 11);
		editPage.assertRow(NEUTROPHIL, 12);
		editPage.assertRow(NEUTROPHIL_IMMATURE, 13);
		editPage.assertRow(LYMPHOCYTES, 14);
		editPage.assertRow(MONOCYTES, 15);
		editPage.assertRow(PLT, 16);
		editPage.assertRow(MPV, 17);
		editPage.assertRow(PDW, 18);		
	}
		
	@Test
	public void verifyValuesAreDefaultedFromPriorSurvey() {
		createNewSurvey();
		FormIndexPage indexPage = journalPage.getFormIndexPage();
		FormEditPage newPage = indexPage.clickNewSurveyButton();
		
		newPage.assertDefaultedValuesExplanationPresent();
		newPage.assertRowShowsDefault(RBC, 1);
		newPage.assertRowShowsDefault(WBC, 8);
		newPage.assertRowShowsDefault(PLT, 14);
	}
	
	@Test
	public void verifyValuesAreValidatedAndConstrained() {
		// Might be better to allow strings to simulate typing different kinds of stuff in.
		// verify that values are constrained to valid values (doubles, longs, percentages)

		FormIndexPage indexPage = journalPage.getFormIndexPage();
		FormEditPage newPage = indexPage.clickNewSurveyButton();
		
		newPage.assertFieldConstrained(RBC, "asdf-10.2", "10.2");
	}
	
	/* Not anymore. They are all bound to the number field. May or may not be able to fix this.
	public void arrowKeysWork() {
		// TODO: This is a minimal test and I already know arrow keys don't work across the row
		// when there's a select control, at least in firefox.
		createNewSurvey();
		FormIndexPage indexPage = journalPage.getFormIndexPage();
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

	private void createNewSurvey() {
		FormIndexPage indexPage = journalPage.getFormIndexPage();
		FormEditPage newPage = indexPage.clickNewSurveyButton();
		
		newPage.setTestDate(TEST_DATE);
		newPage.setRow(RBC, 1);
		newPage.setRow(HB, 2);
		newPage.setRow(HCT, 3);
		newPage.setRow(MCV, 4);
		newPage.setRow(MCH, 5);
		newPage.setRow(RDW, 6);
		newPage.setRow(RET, 7);
		newPage.setRow(WBC, 8);
		newPage.setRow(WBC_DIFF, 9);
		newPage.setRow(NEUTROPHIL, 10);
		newPage.setRow(NEUTROPHIL_IMMATURE, 11);
		newPage.setRow(LYMPHOCYTES, 12);
		newPage.setRow(MONOCYTES, 13);
		newPage.setRow(PLT, 14);
		newPage.setRow(MPV, 15);
		newPage.setRow(PDW, 16);
		newPage.submit();
	}
	
	@Test
	public void fieldsAreValidated() {
		// TODO
	}
}
