package org.sagebionetworks.bridge.webapp.integration.pages;

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

	WebDriverFacade enterField(String cssSelector, String value) {
		driver.findElement(By.cssSelector(cssSelector)).sendKeys(value);
		return this;
	}
	
	void submit(String cssSelector) {
		WebElement form = driver.findElement(By.cssSelector(cssSelector));
		form.findElement(By.cssSelector("button[type=submit]")).click();
	}
	void click(String cssSelector) {
		driver.findElement(By.cssSelector(cssSelector)).click();
	}
	void assertErrorMessage(String cssSelector, String message) {
		waitUntil(cssSelector);
		WebElement errors = driver.findElement(By.cssSelector(cssSelector));
		Assert.assertTrue("Correct error ('"+message+"') in " + cssSelector, errors.getText().contains(message));		
	}
	void waitUntil(final String cssSelector) {
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return (d.findElement(By.cssSelector(cssSelector)) != null);
			}
		});	
	}
	void waitForTitle(final String title) {
		(new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().contains(title);
			}
		});
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
		waitForTitle("Patients & Researchers in Partnership");
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
		waitForTitle("Reset Password");
		return new ResetPasswordPage(this);
	}
	public TermsOfUsePage waitForTOUPage() {
		waitForTitle("Terms of Use");
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
	
	public CommunityPage getCommunityPage() {
		get("/communities/1.html");
		return new CommunityPage(this);
	}
	public ErrorPage getErrorPage(){ 
		waitUntil("h3#error-pane");
		return new ErrorPage(this);
	}
	public ProfilePage getProfilePage() {
		get("/profile.html");
		return new ProfilePage(this);
	}
	public RequestResetPasswordPage getRequestResetPasswordPage() {
		get("/requestResetPassword.html");
		return new RequestResetPasswordPage(this);
	}
	public ResetPasswordPage getResetPasswordPage(boolean includeToken) {
		if (includeToken) {
			get("/resetPassword.html?token=foo");
		} else {
			get("/resetPassword.html");	
		}
		return new ResetPasswordPage(this);
	}
	public SignInPage getSignInPage() {
		get("/signIn.html");
		return new SignInPage(this);
	}
	public SignOutPage getSignOutPage() {
		get("/signOut.html");
		return new SignOutPage(this);
	}
	public SignUpPage getSignUpPage() {
		get("/signUp.html");
		return new SignUpPage(this);
	}
	public TermsOfUsePage getTermsOfUsePage() {
		get("/termsOfUse.html");
		return new TermsOfUsePage(this);
	}

	public void assertNotice(String message) {
		// This is the only way I've found to extract this information from the page.
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-info.humane")));
		String content = (String)((JavascriptExecutor)driver).executeScript("return document.querySelector('#notice').textContent");
		Assert.assertTrue("User notified with message '"+message+"'", content.contains(message));
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
