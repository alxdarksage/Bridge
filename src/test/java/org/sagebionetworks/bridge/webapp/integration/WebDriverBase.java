package org.sagebionetworks.bridge.webapp.integration;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class WebDriverBase {

	protected WebDriverFacade _driver;

	protected WebDriverFacade initDriver() {
		// Does not work with version 26.0 of Firefox on my machine. 
		// Downgraded Firefox to version 25.0 for the time being.
		if (StackConfiguration.isDevelopStack()) {
			_driver = createFirefoxDriver();
		} else {
			_driver = createPhantomJSDriver();
		}
		Window window = _driver.manage().window();
		window.setSize(new Dimension(1024,400));
		_driver.manage().deleteAllCookies();
		_driver.manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
		return _driver;
	}
	
	private WebDriverFacade createPhantomJSDriver() {
		// Because system properties with periods in them are not portable to Bash.
		if (System.getProperty("phantomjs.binary.path") == null && 
			System.getProperty("PHANTOMJS_BINARY_PATH") != null) {
			System.setProperty("phantomjs.binary.path", System.getProperty("PHANTOMJS_BINARY_PATH"));
		}
		return new WebDriverFacade(new PhantomJSDriver());
	}
	
	private WebDriverFacade createFirefoxDriver() {
		return new WebDriverFacade(new FirefoxDriver());
	}
	
	@After
	public void closeDriver() {
		_driver.takeScreenshot();
		_driver.close();
		_driver.quit();
	}

	protected String getUniqueUserName() {
		return "test" + Long.toString(new Date().getTime());
	}
	
	protected String getUniqueEmail() {
		return "test" + Long.toString(new Date().getTime()) + "@test.com";
	}
}
