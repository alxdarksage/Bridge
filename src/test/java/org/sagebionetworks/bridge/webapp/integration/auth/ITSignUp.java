package org.sagebionetworks.bridge.webapp.integration.auth;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.TestEnvironment;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
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
	public void signUpRejectsIfOneFieldLeftBlank() {
		SignUpPage page = driver.getSignUpPage();
		page.setUserName("");
		page.setEmail("bob@bobcat.com");
		page.submit();
		page.assertUserNameError();
		
		page.setUserName("dude");
		page.setEmail("");
		page.submit();
		page.assertEmailError();
	}
	
	@Test
	public void signUpRejectsInvalidUserName() {
		driver.getPortalPage();
		SignUpPage page = driver.getSignUpPage();

		page.setUserName("Tim Powers");
		page.setEmail("powers@powers.com");
		page.submit();
		page.assertInvalidUserNameError();
	}
	
	@Test
	public void signUpShowsNotice() {
		driver.getPortalPage();
		SignUpPage page = driver.getSignUpPage();
		
		page.setUserName( getUniqueUserName() );
		page.setEmail( getUniqueEmail() );
		page.submit();

		driver.waitForPortalPage();
		driver.assertNotice("sent you an email with instructions on completing your registration");
	}

	@Test
	public void signUpRejectsDuplicateNameOrEmail() {
		driver.getPortalPage();
		SignUpPage page = driver.getSignUpPage();
		page.setUserName("timpowers");
		page.setEmail("timpowers@timpowers.com");
		page.submit();
		
		page.assertUserNameDuplicateError();
		page.assertEmailDuplicateError();
	}
	
	@Test
	public void firstTimeSignInShowsTOU() {
		// Why: You are sent an email with a token to reset the password, and that's
		// not available.
		if (TestEnvironment.isUI()) {
			SignUpPage page = driver.getSignUpPage();
			
			// Needs to be random because server is not being start/stopped when 
			// I'm testing during development, only when Maven does the thing.
			String userName = getUniqueUserName();
			String email = getUniqueEmail();
			
			page.setUserName(userName);
			page.setEmail(email);
			page.submit();
			
			driver.waitForPortalPage();
			
			driver.getCommunityPage();
			
			SignInPage signInPage = driver.waitForSignInPage();
			signInPage.signIn(userName, "password");
			
			TermsOfUsePage touPage = driver.waitForTOUPage();

			// cancel
			touPage.clickCancel();
			
			signInPage = driver.waitForSignInPage();
			signInPage.signIn(userName, "password");
			
			// submit without accepting terms of use
			touPage = driver.waitForTOUPage();
			touPage.submit();
			touPage.assertAgreementError();
			
			// now agree and submit
			touPage.checkTermsOfUseAgreement();
			touPage.submit();
			
			CommunityPage communityPage = driver.waitForCommunityPage();
			communityPage.getProfilePanelPage().clickSignOut();

			// Signing back in, no more terms of service
			SignOutPage signOutPage = driver.waitForSignedOutPage();
			signOutPage.login(userName, "password");
			driver.waitForCommunityPage();
		}
	}
	
	@Test
	public void signingUpForExistingEmailFailsWithError() {
		SignUpPage page = driver.getSignUpPage();
		page.setEmail("timpowers@timpowers.com");
		page.setUserName("timpowers");
		page.submit();
		page.assertEmailDuplicateError();
	}
	
}
