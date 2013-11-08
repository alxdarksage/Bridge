package org.sagebionetworks.bridge.webapp;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ITSignUp extends WebDriverBase {
	
	public static class SignUpDriver extends WebDriverFacade {
		public SignUpDriver(WebDriverFacade facade) {
			super(facade);
		}
		public void email(String value) {
			super.enterField("#email", value);
		}
		public void userName(String value) {
			super.enterField("#displayName", value);
		}
		public void submit() {
			super.submit("#signUpForm");
		}
	}
	
	private SignUpDriver driver;

	@Before
	public void createDriver() {
		driver = new SignUpDriver(initDriver());
	}
	
	@Test
	public void signUpFormRejectsEmpty() {
		driver.get("/signUp.html");
		driver.submit();
		
		driver.waitForErrorOn("email", "Enter a valid email address");
		driver.waitForErrorOn("displayName", "Enter a user name");
	}
	
	@Test
	public void signUpFormRejectsIfOneFieldInError() {
		driver.get("/signUp.html");
		
		driver.email("bob@bobcat.com");
		driver.submit();
		
		driver.waitForErrorOn("displayName", "Enter a user name");
	}
	
	@Test
	public void signUpShowsNotice() {
		driver.get("/signUp.html");
		
		driver.email("bob@bobcat.com");
		driver.userName("bobcat");
		driver.submit();
		
		driver.waitForPortalPage();
		driver.waitForNotice("");
	}
	
	@Test
	public void firstTimeSignInShowsTOU() {
		driver.get("/signUp.html");
		
		// Needs to be random because server is not being start/stopped when 
		// I'm testing during development, only when Maven does the thing.
		String email = "test" + Long.toString(new Date().getTime()) + "@test.com";
		
		driver.email(email);
		driver.userName("testdude");
		driver.submit();
		driver.waitForPortalPage();

		signOut();
		signIn(email, "testdude");
		driver.waitForTOUPage();
		
		// cancel
		driver.click("#cancelButton");
		driver.waitForCommunityPage();
		
		// continue without accepting terms of use
		signIn(email, "testdue");
		driver.waitForTOUPage();
		driver.submit("#termsOfUseForm");
		driver.waitForErrorOn("acceptTermsOfUse", "You must accept the terms of use before signing in to Bridge.");
		
		// check and continue
		driver.click("#acceptTermsOfUse");
		driver.submit("#termsOfUseForm");
		driver.waitForCommunityPage();

		signOut();
		signIn(email, "testdude");
		driver.waitForCommunityPage();
	}
	
}
