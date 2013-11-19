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
		page.login("timpowers", null);
		page.assertEmailError();
		page.assertPasswordError();
	}
	
	@Test
	public void dedicatedSignInFormRejectsUnregisteredUser() {
		SignInPage page = driver.getSignInPage();
		page.login("dudeski@dudeski.com","password");
		page.assertGlobalError();
	}
	
	@Test
	public void dedicatedSignInFormRedirectsCorrectly() {
		driver.getCommunityPage();
		
		SignInPage page = driver.getSignInPage();
		page.login("timpowers@timpowers.com", "password");
		driver.waitForCommunityPage();
		
		// Once logged in, you are always redirected from this page.
		driver.getSignInPage();
		driver.waitForCommunityPage();
	}
	
	@Test
	public void youAreRedirectedToLogInIfYouAreNot() {
		driver.get("/profile.html");
		SignInPage page = driver.waitForSignInPage();
		page.login("timpowers@timpowers.com","password");
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
		driver.get("/communities/1.html");
		
		SignInPage page = driver.getSignInPage();
		page.login("octaviabutler@octaviabutler.com", "password");
		
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
		
		cpage.getEmbeddedSignIn().login("timpowers", null);
		
		SignInPage spage = driver.waitForSignInPage();
		spage.assertEmailError();
		spage.assertPasswordError();
	}
	
	@Test
	public void embeddedSignInFormRejectsUnregisteredUser() {
		CommunityPage cpage = driver.getCommunityPage();
		
		cpage.getEmbeddedSignIn().login("dudeski@dudeski.com", "password");
		
		SignInPage spage = driver.waitForSignInPage();
		spage.assertGlobalError();
	}
	
	@Test
	public void embeddedSignInFormRedirectsCorrectly() {
		CommunityPage cpage = driver.getCommunityPage();
		cpage.getEmbeddedSignIn().login("timpowers@timpowers.com", "password");
		
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
		cpage.getEmbeddedSignIn().login("octaviabutler@octaviabutler.com", "password");
		driver.waitForTOUPage();
	}
	
}
