package org.sagebionetworks.bridge.webapp;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class WebDriverBase {

	protected WebDriverFacade _driver;

	protected WebDriverFacade initDriver() {
		// Because system properties with periods in them are not portable to Bash.
		if (System.getProperty("phantomjs.binary.path") == null && 
			System.getProperty("PHANTOMJS_BINARY_PATH") != null) {
			System.setProperty("phantomjs.binary.path", System.getProperty("PHANTOMJS_BINARY_PATH"));
		}
		System.out.println( System.getProperty("phantomjs.binary.path") );
		_driver = new WebDriverFacade(new PhantomJSDriver());
		// System.setProperty("webdriver.chrome.driver", "/Users/alxdark/bin/chromedriver");
		//_driver = new WebDriverFacade(new ChromeDriver());
		// _driver = new WebDriverFacade(new FirefoxDriver());
		Window window = _driver.manage().window();
		window.setSize(new Dimension(968,400));
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
