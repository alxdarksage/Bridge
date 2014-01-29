package org.sagebionetworks.bridge.webapp.integration.pages;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.collect.Sets;

/**
 * Add tons of convenience methods to the driver object to make the 
 * tests easier to write and a lot more readable.
 */
public class WebDriverFacade implements WebDriver {
	
	public static final String CONTEXT_PATH = "http://localhost:8888/bridge";
	public static final int TIMEOUT = 30;
	
	private WebDriver driver;

	public WebDriverFacade(WebDriver driver) {
		this.driver = driver;
	}
	
	public boolean isGhostdriver() {
		return (driver instanceof PhantomJSDriver);
	}

	public void takeScreenshot() {
		String date = Long.toString(new Date().getTime());
		takeScreenshot((driver.getTitle()+" - "+date).replace(" ",""));
	}
	
	public void takeScreenshot(String name) {
		try {
			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			File destFile = new File("./target/images/"+name+".png");
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Assumes the input is surrounded by a div with some kind of selectable title.
	WebElement findCheckbox(String name) {
		String cssSelector = "div[title*=\""+name+"\"] input";
		waitUntil(cssSelector);
		return driver.findElement(By.cssSelector(cssSelector));
	}
	
	void enterField(String cssSelector, String value) {
		waitUntil(cssSelector);
		WebElement target = driver.findElement(By.cssSelector(cssSelector));
		try {
			target.clear(); // This may trigger validation	
		} catch(WebDriverException e) {
			// readonly fields cannot be cleared, that's okay. 
			// Tests must pass with this being true.
		}
		target.sendKeys(value);
	}
	String getFieldValue(String cssSelector) {
		waitUntil(cssSelector);
		return driver.findElement(By.cssSelector(cssSelector)).getAttribute("value");
	}
	void submit(String formCssSelector) {
		waitUntil(formCssSelector);
		WebElement form = driver.findElement(By.cssSelector(formCssSelector));
		form.findElement(By.cssSelector("button[type=submit]")).click();
	}
	void click(String cssSelector) {
		waitUntil(cssSelector);
		driver.findElement(By.cssSelector(cssSelector)).click();
	}
	void clickAndDismissConfirmation(String cssSelector) {
		WebElement element = findElement(By.cssSelector(cssSelector));
		clickAndDismissConfirmation(element);
	}
	void clickAndDismissConfirmation(WebElement element) {
		// Ghostdriver/PhantomJS do not support alerts, so we have to do some branching here.
		if (isGhostdriver()) {
			executeJavaScript("window.alert = function(){}");
			executeJavaScript("window.confirm = function(){return true;}");
		}
		element.click();
		if (!isGhostdriver()) {
			Alert alert = driver.switchTo().alert();
			alert.accept();
		}
	}
	void assertErrorMessage(String cssSelector, String message) {
		waitUntil(cssSelector);
		WebElement errors = driver.findElement(By.cssSelector(cssSelector));
		Assert.assertTrue("Correct error ('"+message+"') in " + cssSelector, errors.getText().contains(message));		
	}
	void assertHeader(String header) {
		waitUntil("h3");
		WebElement h3 = driver.findElement(By.cssSelector("h3"));
		Assert.assertTrue("Correct header ('"+header+"')", h3.getText().contains(header));
	}
	void assertFieldValue(String cssSelector, String expectedValue) {
		waitUntil(cssSelector);
		String valueInForm = getFieldValue(cssSelector);
		Assert.assertEquals("Correct value", expectedValue, valueInForm);
	}
	void assertCssClass(String cssSelector, String cssClass) {
		waitUntil(cssSelector);
		WebElement element = driver.findElement(By.cssSelector(cssSelector));
		String classTokens = element.getAttribute("class");
		if (classTokens == null) {
			Assert.fail("Contains CSS class: " + cssClass + ", but no class attribute");
		}
		Set<String> tokens = Sets.newHashSet(classTokens.split("\\s+"));
		Assert.assertTrue("Contain CSS class: " + cssClass, tokens.contains(cssClass));
	}
	void waitUntil(final String cssSelector) {
		(new WebDriverWait(driver, TIMEOUT)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return (d.findElement(By.cssSelector(cssSelector)) != null);
			}
		});	
	}
	void waitUntilPartialLink(final String partialLinkText) {
		(new WebDriverWait(driver, TIMEOUT)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return (d.findElement(By.partialLinkText(partialLinkText)) != null);
			}
		});	
	}
	void waitForTitle(final String title) {
		(new WebDriverWait(driver, TIMEOUT)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().contains(title);
			}
		});
	}
	void waitForHeader(final String header) {
		(new WebDriverWait(driver, TIMEOUT)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.findElement(By.tagName("h3")).getText().contains(header);
			}
		});
	}
	
	public String executeJavaScriptForString(String javascript) {
		if (driver instanceof JavascriptExecutor) {
			return ((JavascriptExecutor) driver).executeScript(javascript).toString();
		} else {
			throw new RuntimeException("Cannot execute JS using this Selenium driver");
		}
	}
	public void executeJavaScript(String javascript) {
		if (driver instanceof JavascriptExecutor) {
			((JavascriptExecutor) driver).executeScript(javascript);
		} else {
			throw new RuntimeException("Cannot execute JS using this Selenium driver");
		}
	}
	public AdminPage waitForAdminPage() {
		waitForTitle(AdminPage.TITLE);
		return new AdminPage(this);
	}
	public CommunitiesAdminPage waitForCommunitiesAdminPage() {
		waitForTitle(CommunitiesAdminPage.TITLE);
		return new CommunitiesAdminPage(this);
	}
	public CommunityPage waitForCommunityPage() {
		waitForTitle(CommunityPage.TITLE);
		return new CommunityPage(this);
	}
	public ErrorPage waitForErrorPage() {
		waitUntil("h3#error-pane");
		return new ErrorPage(this);
	}
	public PortalPage waitForPortalPage() {
		waitForTitle(PortalPage.TITLE);
		return new PortalPage(this);
	}
	public ProfilePage waitForProfilePage() {
		waitForTitle(ProfilePage.TITLE);
		return new ProfilePage(this);
	}
	public RequestResetPasswordPage waitForRequestResetPasswordPage() {
		waitForTitle(RequestResetPasswordPage.TITLE);
		return new RequestResetPasswordPage(this);
	}
	public ResetPasswordPage waitForResetPasswordPage() {
		waitForTitle(ResetPasswordPage.TITLE);
		return new ResetPasswordPage(this);
	}
	public TermsOfUsePage waitForTOUPage() {
		waitForTitle(TermsOfUsePage.TITLE);
		return new TermsOfUsePage(this);
	}
	public SignInPage waitForSignInPage() {
		waitForTitle(SignInPage.TITLE);
		return new SignInPage(this);
	}
	public SignOutPage waitForSignedOutPage() {
		waitForTitle(SignOutPage.TITLE);
		return new SignOutPage(this);
	}
	public SignUpPage waitForSignUpPage() {
		waitForTitle(SignUpPage.TITLE);
		return new SignUpPage(this);
	}
	public JournalPage waitForJournalHomePage() {
		waitForTitle(JournalPage.TITLE);
		return new JournalPage(this);
	}
	
	public AdminPage getAdminPage() {
		get(AdminPage.URL);
		return new AdminPage(this);
	}
	public CommunitiesAdminPage getCommunitiesAdminPage() {
		get(CommunitiesAdminPage.URL);
		return new CommunitiesAdminPage(this);
	}
	public CommunityPage getCommunityPage() {
		get(CommunityPage.URL);
		return new CommunityPage(this);
	}
	public ErrorPage getErrorPage(){ 
		waitUntil("h3#error-pane");
		return new ErrorPage(this);
	}
	public PortalPage getPortalPage() {
		get(PortalPage.URL);
		return new PortalPage(this);
	}
	public ProfilePage getProfilePage() {
		get(ProfilePage.URL);
		return new ProfilePage(this);
	}
	public RequestResetPasswordPage getRequestResetPasswordPage() {
		get(RequestResetPasswordPage.URL);
		return new RequestResetPasswordPage(this);
	}
	public ResetPasswordPage getResetPasswordPage(boolean includeToken) {
		if (includeToken) {
			get(ResetPasswordPage.URL+"?token=testToken");
		} else {
			get(ResetPasswordPage.URL);
		}
		return new ResetPasswordPage(this);
	}
	public SignInPage getSignInPage() {
		get(SignInPage.URL);
		return new SignInPage(this);
	}
	public SignOutPage getSignOutPage() {
		get(SignOutPage.URL);
		return new SignOutPage(this);
	}
	public SignUpPage getSignUpPage() {
		get(SignUpPage.URL);
		return new SignUpPage(this);
	}
	public TermsOfUsePage getTermsOfUsePage() {
		get(TermsOfUsePage.URL);
		return new TermsOfUsePage(this);
	}
	public JournalPage getJournalPage() {
		get(JournalPage.URL);
		return new JournalPage(this);
	}

	public void assertNotice(String message) {
		// This is the only way I've found to extract this information from the page.
		(new WebDriverWait(driver, TIMEOUT)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-info.humane")));
		String content = (String)((JavascriptExecutor)driver).executeScript("return document.querySelector('#notice').textContent");
		Assert.assertTrue("User notified with message '"+message+"' was: " + content, content.contains(message));
	}
	
	// Pass-throughs to the driver object. Arguably, these should be removed and replaced
	// with methods that *always* wait first, because this has been an issue in getting 
	// to reliable tests.
	
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
		driver.get(CONTEXT_PATH + arg0);
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
