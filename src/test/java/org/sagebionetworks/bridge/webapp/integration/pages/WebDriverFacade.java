package org.sagebionetworks.bridge.webapp.integration.pages;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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
	
	void takeScreenshot() {
		try {
			String name = Long.toString(new Date().getTime());
			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			File destFile = new File(name + ".png");
			System.out.println(destFile.getAbsolutePath());
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	WebDriverFacade enterField(String cssSelector, String value) {
		waitUntil(cssSelector);
		driver.findElement(By.cssSelector(cssSelector)).sendKeys(value);
		return this;
	}
	
	void submit(String cssSelector) {
		waitUntil(cssSelector);
		WebElement form = driver.findElement(By.cssSelector(cssSelector));
		form.findElement(By.cssSelector("button[type=submit]")).click();
	}
	void click(String cssSelector) {
		waitUntil(cssSelector);
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
	public CommunityAdminPage waitForCommunityAdminPage() {
		waitForTitle(CommunityAdminPage.TITLE);
		return new CommunityAdminPage(this);
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
	public CommunityWikiPage waitForCommunityWikiPage() {
		waitForTitle(CommunityWikiPage.TITLE);
		return new CommunityWikiPage(this);
	}
	
	public AdminPage getAdminPage() {
		get("/admin/index.html");
		return new AdminPage(this);
	}
	public CommunitiesAdminPage getCommunitiesAdminPage() {
		get("/admin/communities.html");
		return new CommunitiesAdminPage(this);
	}
	public CommunityPage getCommunityPage() {
		get("/communities/1.html");
		return new CommunityPage(this);
	}
	public CommunityAdminPage getCommunityAdminPage() {
		get("/admin/communities/1.html");
		return new CommunityAdminPage(this);
	}
	
	public ErrorPage getErrorPage(){ 
		waitUntil("h3#error-pane");
		return new ErrorPage(this);
	}
	public PortalPage getPortalPage() {
		get("/portal/index.html");
		return new PortalPage(this);
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
	public CommunityWikiPage getCommunityWikiPage() {
		// This is delicate!
		get("/communities/1/wikis/6/edit.html");
		return new CommunityWikiPage(this);
	}

	public void assertNotice(String message) {
		// This is the only way I've found to extract this information from the page.
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-info.humane")));
		String content = (String)((JavascriptExecutor)driver).executeScript("return document.querySelector('#notice').textContent");
		Assert.assertTrue("User notified with message '"+message+"' was: " + content, content.contains(message));
	}
	
	/**
	 * PhantomJS cannot handle alerts and dialogs. This has to happen after a page loads.
	 */
	private void applyGhostdriverFix() {
		executeJavaScript("window.alert = function(){}");
		executeJavaScript("window.confirm = function(){return true;}");
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
