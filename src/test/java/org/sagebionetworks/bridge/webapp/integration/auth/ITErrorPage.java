package org.sagebionetworks.bridge.webapp.integration.auth;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.ErrorPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

public class ITErrorPage extends WebDriverBase {
	
	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@After
	@Override
	public void closeDriver() {
		_driver.close();
		_driver.quit();
	}	
	
	@Test
	public void errorPageFor404() {
		driver.get("/foo.html");
		
		ErrorPage page = driver.getErrorPage();
		page.assertErrorTitle("Not Found");
	}
	
	@Test
	public void noPageReturnsIndexPageNotErrorPage() {
		driver.get("/portal/");
		driver.waitForPortalPage();
	}
}
