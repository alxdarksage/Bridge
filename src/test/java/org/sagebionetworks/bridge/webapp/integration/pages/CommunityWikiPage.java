package org.sagebionetworks.bridge.webapp.integration.pages;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.GetText;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CommunityWikiPage extends EmbeddedSignInPage {
	
	public static final String TITLE = "Fanconi Anemia Site Editor";

	public CommunityWikiPage(WebDriverFacade facade) {
		super(facade);
	}

	public void clickAllPagesTab() {
		facade.click("#allPagesAct");
	}

	public void clickNavigationTab() {
		facade.click("#navAct");
	}
	
	public void clickNewPage() {
		facade.click("#newPageAct");
	}
	
	public void clickEditIndex() {
		facade.click("#editIndexAct");
	}
	
	public void clickQuitEditing() {
		facade.click("#quitAct");
	}
	
	public void submit() {
		facade.submit("#wikiForm");
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}
	
	public void setTitle(String value) {
		facade.enterField("#title", value);
	}
	
	public void setHTML(String html) {
		waitForEditorToLoad();
		html = html.replaceAll("\"", "&quot;");
		facade.executeJavaScript("CKEDITOR.instances.markdown.setData(\""+html+"\");");
	}
	
	public void clickPageToEdit(String pageName) {
		findPageLink(pageName).click();
	}
	
	public void clickInsertImageButton() {
		waitForEditorToLoad();
		facade.findElement(By.cssSelector("a[title='Image']")).click();
	}
	
	public void clickInsertLinkButton() {
		waitForEditorToLoad();
		facade.findElement(By.cssSelector("a[title='Link']")).click();
	}
	public void clickDialogPagesTab() {
		waitForDialogToLoad();
		facade.findElement(By.cssSelector("div.cke_editor_markdown_dialog a[title='Community Pages']")).click();
	}
	
	public void clickDialogBrowseServerButton() {
		waitForDialogToLoad();
		facade.findElement(By.cssSelector("div.cke_editor_markdown_dialog a[title='Browse Server']")).click();
	}
	
	public void clickDialogOkButton() {
		waitForDialogToLoad();
		facade.findElement(By.cssSelector("div.cke_editor_markdown_dialog a[title='OK']")).click();
	}
	
	public void clickDialogLinkToPage(String name) {
		waitForDialogToLoad();
		facade.findElement(By.cssSelector("div.cke_editor_markdown_dialog div[name='pagesTab'] a[title='"+name+"']")).click();
	}
	
	public String getLinkForPage(String pageName) throws MalformedURLException {
		String link = findPageLink(pageName).getAttribute("href");
		URL url = new URL(link);
		String path = url.getPath();
		return path.replace("/edit","");
	}
	
	public void assertThereIsNoScriptInEditor() {
		waitForEditorToLoad();
		setSageTestValueToEditorData();
		String value = getSageTestValue();
		Assert.assertTrue("There is no script in editor", value.indexOf("script") == -1);
	}
	
	public void assertThereIsLinkInEditor() {
		waitForEditorToLoad();
		setSageTestValueToEditorData();
		// String value = facade.findElement(By.id("sageTestValue")).getText();
		String value = getSageTestValue();
		Assert.assertTrue("There is a link in the text", value.indexOf("</a>") > -1);
	}
	
	public void assertPageExists(String pageName) {
		// This throws an error if the link is not there, which we want
		findPageLink(pageName);
	}
	
	public void assertPageDoesNotExist(String pageName) {
		try {
			findPageLink(pageName);
			Assert.fail("Should have thrown an exception");
		} catch(Exception e) {
		}
	}
	
	public void assertNavEntryExists(String entry) {
		findNavEntry(entry);
	}

	public void assertNavEntryDoesNotExist(String entry) {
		try {
			findNavEntry(entry);
			Assert.fail("Should have thrown an exception");
		} catch(Exception e) {
		}
	}
	
	public void assertText(String text) {
		facade.waitUntil(".main-pane");
		WebElement base = facade.findElement(By.cssSelector(".main-pane"));
		base.findElement(By.xpath("//div[contains(.,'"+text+"')]"));
	}
	
	public void selectPage(String pageName) {
		// This throws an error if the link is not there, which we want
		findPageLink(pageName).click();
	}
	
	public void deletePage(String pageName) {
		facade.clickAndDismissConfirmation(findPageDeleteLink(pageName));
	}
	
	public void assertTitleError() {
		facade.assertErrorMessage("#title_errors", "The page requires a title");
	}
	
	public void assertMarkdownError() {
		facade.assertErrorMessage("#markdown_errors", "This page should have some kind of content");
	}
	
	public void assertIndexToolbars() {
		waitForEditorToLoad();
		setSageTestValueToToolbarLength();
		// Stopped working for reasons that aren't clear
		// String value = facade.findElement(By.id("sageTestValue")).getText();
		String value = getSageTestValue();
		Assert.assertTrue("Toolbar has only 1 element", "1".equals(value));
	}
	
	public void assertFullEditorToolbars() {
		waitForEditorToLoad();
		setSageTestValueToToolbarLength();
		// Stopped working for reasons that aren't clear
		// String value = facade.findElement(By.id("sageTestValue")).getText();
		String value = getSageTestValue();
		Assert.assertTrue("Toolbar has many elements", "7".equals(value));
	}
	
	// Using the API for the next three methods just started failing, and after many hours I could not identify a cause;
	
	private String getSageTestValue() {
		return facade.executeJavaScriptForString("return document.getElementById('sageTestValue').textContent;");
	}
	
	private void setSageTestValueToToolbarLength() {
		facade.executeJavaScript("document.getElementById('sageTestValue').textContent=CKEDITOR.instances.markdown.toolbar.length");
	}
	
	private void setSageTestValueToEditorData() {
		facade.executeJavaScript("document.getElementById('sageTestValue').textContent=CKEDITOR.instances.markdown.getData()");
	}
	
	public void waitForEditorToLoad() {
		(new WebDriverWait(facade, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				// For some reason this has stopped working.
				//String value = facade.findElement(By.id("sageEditorReady")).getText();
				String value = facade.executeJavaScriptForString("return document.getElementById('sageEditorReady').textContent");
				return ("true".equals(value));
			}
		});			
	}
	
	public void waitForDialogToLoad() {
		(new WebDriverWait(facade, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				// Same here, stopped working reliably.
				// String value = facade.findElement(By.id("sageDialogOpen")).getText();
				String value = facade.executeJavaScriptForString("return document.getElementById('sageDialogOpen').textContent");
				return ("true".equals(value));
			}
		});
	}
	
	private WebElement findPageLink(String name) {
		facade.waitUntil("div[title='"+name+"'] a.linkAct");
		return facade.findElement(By.cssSelector("div[title='"+name+"'] a.linkAct"));
	}
	
	private WebElement findNavEntry(String entry) {
		facade.waitUntil("#user-nav");
		WebElement base = facade.findElement(By.id("user-nav"));
		return base.findElement(By.xpath("//div[contains(.,'"+entry+"')]"));
	}
	
	private WebElement findPageDeleteLink(String name) {
		facade.waitUntil("div[title='"+name+"'] a.deleteAct");
		return facade.findElement(By.cssSelector("div[title='"+name+"'] a.deleteAct"));
	}
}
