package org.sagebionetworks.bridge.webapp.integration;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class WebDriverBase {

	protected WebDriverFacade _driver;
	
	@Rule public ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule();
	
	protected WebDriverFacade initDriver() {
		// Does not work with version 26.0 of Firefox on my machine. 
		// Downgraded Firefox to version 25.0 for the time being.
		if (StackConfiguration.isDevelopStack()) {
			_driver = createFirefoxDriver();
		} else {
			_driver = createPhantomJSDriver();
		}
		Window window = _driver.manage().window();
		// NOTE: With the introduction of a mobile drawer that slides out from the left (Snap.js),
		// the viewport is fixed in height such that elements off the page appear to be "invisible"
		// to WebDriver, and tests fail. So the screen has to be high enough to see everything on 
		// the page.
		window.setSize(new Dimension(1024,1400));
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

	protected String getUniqueUserName() {
		return "test" + Long.toString(new Date().getTime());
	}
	
	protected String getUniqueEmail() {
		return "test" + Long.toString(new Date().getTime()) + "@test.com";
	}
	
	public class ScreenshotTestRule implements MethodRule {
		public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					try {
						statement.evaluate();
					} catch (Throwable t) {
						takeScreenshot(frameworkMethod.getName());
						throw t;
					} finally {
						//_driver.close();
						//_driver.quit();
					}
				}

				public void takeScreenshot(String fileName) throws Throwable {
					try {
						File srcFile = ((TakesScreenshot)_driver.getDriver()).getScreenshotAs(OutputType.FILE);
						File destFile = new File("./target/images/"+fileName+".png");
						FileUtils.copyFile(srcFile, destFile);
					} catch (Throwable t) {
						throw t;
					}
				}
			};
		}
	}
	
}
