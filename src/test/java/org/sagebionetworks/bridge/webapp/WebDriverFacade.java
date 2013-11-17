package org.sagebionetworks.bridge.webapp;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
	public WebDriverFacade(WebDriverFacade facade) {
		this.driver = facade.driver;
	}

	protected WebDriverFacade enterField(String cssSelector, String value) {
		driver.findElement(By.cssSelector(cssSelector)).sendKeys(value);
		return this;
	}
	
	protected void submit(String cssSelector) {
		WebElement form = driver.findElement(By.cssSelector(cssSelector));
		form.findElement(By.cssSelector("button[type=submit]")).click();
	}
	
	protected void click(String cssSelector) {
		driver.findElement(By.cssSelector(cssSelector)).click();
	}

	protected void assertExists(String cssSelector) {
		WebElement element = driver.findElement(By.cssSelector(cssSelector));
		Assert.assertNotNull("Should exist in page: " + cssSelector, element);
	}
	
	protected void assertMissing(String cssSelector) {
		List<WebElement> list= driver.findElements(By.cssSelector(cssSelector));
		Assert.assertTrue("Should not exist in page: " + cssSelector, list.isEmpty());
	}
	
	protected void assertErrorMessage(String cssSelector, String message) {
		WebElement errors = driver.findElement(By.cssSelector(cssSelector));
		Assert.assertTrue("Correct error ('"+message+"') in " + cssSelector, errors.getText().contains(message));		
	}
	
	protected void waitForPortalPage() {
		waitForTitle("Patients & Researchers in Partnership");
	}
	protected void waitForCommunityPage() {
		waitForTitle("Fanconi Anemia");
	}
	protected void waitForTOUPage() {
		waitForTitle("Terms of Use");
	}
	protected void waitForSignInPage() {
		waitForTitle("Sign In");
	}
	protected void waitForSignedOutPage() {
		waitForTitle("Signed Out");
	}
	protected void waitForSignUpPage() {
		waitForTitle("Sign Up");
	}
	protected void waitForResetPasswordPage() {
		waitForTitle("Reset Password");
	}
	protected void waitForProfilePage() {
		waitForTitle("Profile");
	}
	protected void waitForErrorPage(String error) {
		waitUntil("h3#error-pane");
		assertErrorMessage("#error-pane", error);
	}
	protected void waitForError() {
		waitUntil("div.has-error");
	}
	protected void waitForError(String error) {
		waitForError();
		assertErrorMessage(".has-error", error);
	}	
	protected void waitForErrorOn(String field) {
		if (field.substring(0,1).equals("#")) {
			throw new RuntimeException("waitForErrorOn() doesn't take css selector; use an id token");
		}
		waitUntil("#"+field+"_errors");
	}
	protected void waitForErrorOn(String field, String error) {
		if (field.substring(0,1).equals("#")) {
			throw new RuntimeException("waitForErrorOn() doesn't take css selector; use an id token");
		}
		waitUntil("#"+field+"_errors");
		assertErrorMessage("#"+field+"_errors", error);
	}
	protected void waitForAndAssertNotice(String message) {
		// This is the only way I've found to extract this information from the page.
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-info.humane")));
		String content = (String)((JavascriptExecutor)driver).executeScript("return document.querySelector('#notice').textContent");
		Assert.assertTrue("User notified with message '"+message+"'", content.contains(message));
	}
	private void waitUntil(final String cssSelector) {
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return (d.findElement(By.cssSelector(cssSelector)) != null);
			}
		});	
	}
	
	protected void waitForTitle(final String title) {
		(new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().contains(title);
			}
		});
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
		return driver.findElements(arg0);
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
