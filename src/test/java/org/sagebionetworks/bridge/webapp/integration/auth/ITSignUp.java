package org.sagebionetworks.bridge.webapp.integration.auth;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignOutPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignUpPage;
import org.sagebionetworks.bridge.webapp.integration.pages.TermsOfUsePage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITSignUp extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void signUpFormRejectsEmpty() {
		SignUpPage page = driver.getSignUpPage();
		page.submit();
		
		page.assertEmailError();
		page.assertUserNameError();
	}
	
	@Test
	public void signUpFormRejectsIfOneFieldInError() {
		SignUpPage page = driver.getSignUpPage();
		page.setEmail("bob@bobcat.com");
		page.submit();
		page.assertUserNameError();
	}
	
	@Test
	public void signUpShowsNotice() {
		driver.getPortalPage();
		SignUpPage page = driver.getSignUpPage();
		page.setEmail("bob@bobcat.com");
		page.setUserName("bob");
		page.submit();
		
		driver.waitForPortalPage();
		driver.assertNotice("sent you an email with instructions on completing your registration");
	}
	
	@Test
	public void firstTimeSignInShowsTOU() {
		SignUpPage page = driver.getSignUpPage();
		
		// Needs to be random because server is not being start/stopped when 
		// I'm testing during development, only when Maven does the thing.
		String email = getUniqueEmail();
		
		page.setEmail(email);
		page.setUserName("testdude");
		page.submit();
		
		driver.waitForPortalPage();
		
		CommunityPage communityPage = driver.getCommunityPage();
		communityPage.getEmbeddedSignIn().login(email, "testdude");
		
		TermsOfUsePage touPage = driver.waitForTOUPage();

		// cancel
		touPage.cancel();
		
		communityPage = driver.waitForCommunityPage();
		communityPage.getEmbeddedSignIn().login(email, "testdude");
		
		// submit without accepting terms of use
		touPage = driver.waitForTOUPage();
		touPage.submit();
		touPage.assertAgreementError();
		
		// now agree and submit
		touPage.checkTermsOfUseAgreement();
		touPage.submit();
		
		communityPage = driver.waitForCommunityPage();
		communityPage.getEmbeddedSignIn().clickSignOut();

		// Signing back in, no more terms of service
		SignOutPage signOutPage = driver.waitForSignedOutPage();
		signOutPage.login(email, "testdude");
		driver.waitForCommunityPage();
	}
	
}
