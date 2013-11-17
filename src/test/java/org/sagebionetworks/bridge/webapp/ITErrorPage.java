package org.sagebionetworks.bridge.webapp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
		WebElement element = driver.findElement(By.tagName("h3"));
		Assert.assertEquals("User-friendly error page", "Not Found", element.getText());
	}
	
	@Test
	public void noPageReturnsIndexPageNotErrorPage() {
		driver.get("/portal/");
		driver.waitForPortalPage();
	}
}
