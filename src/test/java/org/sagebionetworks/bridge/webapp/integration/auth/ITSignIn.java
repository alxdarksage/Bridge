package org.sagebionetworks.bridge.webapp.integration.auth;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.SignInPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITSignIn extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void dedicatedSignInFormRejectsEmptyForm() {
		SignInPage page = driver.getSignInPage();
		page.submit();
		page.assertEmailError();
		page.assertPasswordError();
	}
	
	@Test
	public void dedicatedSignInFormRejectsInvalidEmailAddress() {
		SignInPage page = driver.getSignInPage();
		page.signIn("timpowers", null);
		page.assertEmailError();
		page.assertPasswordError();
	}
	
	@Test
	public void dedicatedSignInFormRejectsUnregisteredUser() {
		SignInPage page = driver.getSignInPage();
		page.signIn("dudeski@dudeski.com","password");
		page.assertGlobalError();
	}
	
	@Test
	public void dedicatedSignInFormRedirectsCorrectly() {
		driver.getCommunityPage();
		
		SignInPage page = driver.getSignInPage();
		page.signInAsTimPowers();
		driver.waitForCommunityPage();
		
		// Once logged in, you are always redirected from this page.
		driver.getSignInPage();
		driver.waitForCommunityPage();
	}
	
	@Test
	public void youAreRedirectedToLogInIfYouAreNot() {
		driver.getProfilePage();
		
		SignInPage page = driver.getSignInPage();
		page.signInAsTimPowers();
		driver.waitForPortalPage();
	}
	
	@Test
	public void dedicatedSignInFormLinksToIForgotMyPassword() {
		SignInPage page = driver.getSignInPage();
		page.clickForgotPassword();
		driver.waitForRequestResetPasswordPage();
	}
	
	@Test
	public void dedicatedSignInFormToSignUp() {
		SignInPage page = driver.getSignInPage();
		page.clickSignUp();
		driver.waitForSignUpPage();
	}	
	
	@Test
	public void dedicatedSignInFormRedirectsForTOU() {
		driver.getCommunityPage();
		
		SignInPage page = driver.getSignInPage();
		page.signInAsOctaviaButler();
		
		// Octavia has not signed the TOU.
		driver.waitForTOUPage();
	}
	
	@Test
	public void embeddedSignInFormRejectsEmptyForm() {
		CommunityPage cpage = driver.getCommunityPage();
		
		cpage.getEmbeddedSignIn().submit();
		
		SignInPage spage = driver.waitForSignInPage();
		spage.assertEmailError();
		spage.assertPasswordError();
	}
	
	@Test
	public void embeddedSignInFormRejectsInvalidEmailAddress() {
		CommunityPage cpage = driver.getCommunityPage();
		
		cpage.getEmbeddedSignIn().signIn("timpowers", null);
		
		SignInPage spage = driver.waitForSignInPage();
		spage.assertEmailError();
		spage.assertPasswordError();
	}
	
	@Test
	public void embeddedSignInFormRejectsUnregisteredUser() {
		CommunityPage cpage = driver.getCommunityPage();
		
		cpage.getEmbeddedSignIn().signIn("dudeski@dudeski.com", "password");
		
		SignInPage spage = driver.waitForSignInPage();
		spage.assertGlobalError();
	}
	
	@Test
	public void embeddedSignInFormRedirectsCorrectly() {
		CommunityPage cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().signInAsTimPowers();
		
		driver.waitForCommunityPage();
	}
	
	@Test
	public void embeddedSignInFormLinksToIForgotMyPassword() {
		CommunityPage cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().clickForgotPassword();
		driver.waitForRequestResetPasswordPage();
	}
	
	@Test
	public void embeddedSignInFormToSignUp() {
		CommunityPage cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().clickSignUp();
		driver.waitForSignUpPage();
	}
	
	@Test
	public void embeddedSignInFormRedirectsForTOU() {
		CommunityPage cpage = driver.getCommunityPage();
		
		// Octavia will never sign the TOU (in the stub client).
		cpage.getEmbeddedSignIn().signInAsOctaviaButler();
		driver.waitForTOUPage();
	}
	
}
