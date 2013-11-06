package org.sagebionetworks.bridge.webapp;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Add tons of convenience methods to the driver object to make the 
 * tests easier to write and a lot more readable.
 */
public class WebDriverFacade implements WebDriver {
	
	private WebDriver driver;
	
	public WebDriverFacade(WebDriver driver) {
		this.driver = driver;
	}

	public WebDriverFacade enterForm(String cssSelector, String value) {
		driver.findElement(By.cssSelector(cssSelector)).sendKeys(value);
		return this;
	}
	
	public WebDriverFacade submit(String cssSelector) {
		WebElement form = driver.findElement(By.cssSelector(cssSelector));
		form.findElement(By.cssSelector("button[type=submit]")).click();
		return this;
	}
	
	public void waitUntil(final String cssSelector) {
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return (d.findElement(By.cssSelector(cssSelector)) != null);
			}
		});	
	}

	public void assertErrorMessage(String cssSelector, String message) {
		WebElement errors = driver.findElement(By.cssSelector(cssSelector));
		Assert.assertEquals("Correct error ('"+message+"') in " + cssSelector, errors.getText(), message);		
	}
	
	public void assertTitle(String title) {
		Assert.assertTrue("Title contains: " + title, driver.getTitle().contains(title));
	}
	
	// Pass-throughs to the driver object.
	
	@Override
	public void close() {
		driver.close();
	}
	@Override
	public WebElement findElement(By arg0) {
		return driver.findElement(arg0);
	}
	@Override
	public List<WebElement> findElements(By arg0) {
		return findElements(arg0);
	}
	@Override
	public void get(String arg0) {
		driver.get("http://localhost:8888/webapp" + arg0);
	}
	@Override
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}
	@Override
	public String getPageSource() {
		return driver.getPageSource();
	}
	@Override
	public String getTitle() {
		return driver.getTitle();
	}
	@Override
	public String getWindowHandle() {
		return driver.getWindowHandle();
	}
	@Override
	public Set<String> getWindowHandles() {
		return driver.getWindowHandles();
	}
	@Override
	public Options manage() {
		return driver.manage();
	}
	@Override
	public Navigation navigate() {
		return driver.navigate();
	}
	@Override
	public void quit() {
		driver.quit();
	}
	@Override
	public TargetLocator switchTo() {
		return driver.switchTo();
	}

}
