package org.sagebionetworks.bridge.webapp;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebDriverBase {

	protected WebDriverFacade _driver;

	protected WebDriverFacade initDriver() {
		// System.setProperty("phantomjs.binary.path", "/Users/alxdark/bin/phantomjs");
		// driver = new SignInDriver(new WebDriverFacade(new PhantomJSDriver()));
		// System.setProperty("webdriver.chrome.driver", "/Users/alxdark/bin/chromedriver");
		_driver = new WebDriverFacade(new FirefoxDriver());
		//_driver = new WebDriverFacade(new ChromeDriver());
		_driver.manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
		return _driver;
	}
	
	@After
	public void closeDriver() {
		_driver.assertMissing("#error-pane");
		_driver.close();
		_driver.quit();
	}
	
	
	protected void signIn(String email, String password) {
		_driver.get("/communities/index.html");
		_driver.get("/signIn.html");
		
		_driver.enterField("#email", email);
		_driver.enterField("#password", password);
		_driver.submit("#signInForm");
	}
	protected void signIn() {
		signIn("timpowers@timpowers.com", "password");
	}
	protected void signOut() {
		_driver.get("/signOut.html");
	}	

}
