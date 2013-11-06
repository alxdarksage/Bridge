package org.sagebionetworks.bridge.webapp;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ITSignIn {
	
	private WebDriverFacade driver;

	// These two methods seem a little faster if you don't quit.
	
	@Before
	public void createDriver() {
		driver = new WebDriverFacade(new FirefoxDriver());
		driver.manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
	}
	
	@After
	public void closeDriver() {
		driver.close();
		driver.quit();
	}
	
	@Test
	public void dedicatedSignInFormRejectsEmptyForm() {
		driver.get("/signIn.html");
		driver.assertTitle("Bridge : Sign In");
		
		driver.submit("#signInForm").waitUntil("div.has-error");
		driver.assertErrorMessage("#email_errors", "Enter a valid email address");
		driver.assertErrorMessage("#password_errors", "Enter your password");
	}
	
	@Test
	public void dedicatedSignInFormRejectsInvalidEmailAddress() {
		driver.get("/signIn.html");
		driver.assertTitle("Bridge : Sign In");
		
		driver.enterForm("#email", "timpowers");
		
		driver.submit("#signInForm").waitUntil("div.has-error");
		driver.assertErrorMessage("#email_errors", "Enter a valid email address");
		driver.assertErrorMessage("#password_errors", "Enter your password");
	}
	
	@Test
	public void dedicatedSignInFormRejectsUnregisteredUser() {
		driver.get("/signIn.html");
		
		driver.enterForm("#email", "dude@dude.com");
		driver.enterForm("#password", "password");
		
		driver.submit("#signInForm").waitUntil("#signInForm_errors");
		driver.assertErrorMessage("#signInForm_errors", "Unable to sign in. Email or password may be incorrect.");
	}
	
	@Test
	public void dedicatedSignInFormRedirectsCorrectly() {
		driver.get("/communities/index.html");
		driver.get("/signIn.html");
		
		driver.enterForm("#email", "octaviabutler@octaviabutler.com");
		driver.enterForm("#password", "password");
		driver.submit("#signInForm").waitUntil("#profile-pane");
		driver.assertTitle("Bridge : Fanconi Anemia");

		// Once logged in, you are always redirected from this page.
		driver.get("/signIn.html");
		driver.assertTitle("Bridge : Fanconi Anemia");
	}
}
