package org.sagebionetworks.bridge.webapp.integration.communities;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.integration.WebDriverBase;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityPage;
import org.sagebionetworks.bridge.webapp.integration.pages.CommunityWikiPage;
import org.sagebionetworks.bridge.webapp.integration.pages.WebDriverFacade;

/**
 * 	None of these tests run in GhostDriver right now, so they are disabled. And they 
 * can fail occasionally in the Firefox driver, so there are timing issues.
 *
 */
public class ITCommunityWiki extends WebDriverBase {

	private WebDriverFacade driver;

	@Before
	public void createDriver() {
		driver = initDriver();
	}
	
	@Test
	public void canLoadAndQuitEditor() {
		CommunityPage page = driver.getCommunityPage();
		page.getEmbeddedSignIn().signInAsTimPowers();
		page.clickEditButton();
		
		CommunityWikiPage wpage = driver.waitForCommunityWikiPage();
		wpage.clickQuitEditing();
		
		page = driver.waitForCommunityPage();
	}
	
	@Test
	public void canLoadIndexCkEditor() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		
		wpage.clickNavigationTab();
		wpage.clickEditIndex();
		wpage.assertIndexToolbars();
	}
	
	@Test
	public void canLoadFullCKEditor() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		
		wpage.clickNewPage();
		wpage.assertFullEditorToolbars();
	}
	
	@Test
	public void canCreateAndDeleteNewPage() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNewPage();
		
		wpage.setTitle("New Page");
		wpage.setHTML("<p><strong>New Page</strong> content.</p>");
		wpage.submit();
		
		wpage.assertPageExists("New Page");
		
		wpage.deletePage("New Page");
	}
	
	@Test
	public void validatesPageFields() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNewPage();
		wpage.submit();
		
		wpage.assertTitleError();
		wpage.assertMarkdownError();
	}
	
	@Test
	public void canEditAndRevertNavigationWiki() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNavigationTab();
		wpage.clickEditIndex();
		
		wpage.setHTML("<ul><li><a>Test Entry</a></li></ul>");
		wpage.submit();

		wpage.assertNavEntryExists("Test Entry");
		wpage.clickEditIndex();

		wpage.setHTML("Index");
		wpage.submit();
		wpage.assertNavEntryDoesNotExist("Test Entry");
	}
	
	@Test
	public void quitFromIndexTakesUserToWelcomeWiki() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNavigationTab();
		wpage.clickEditIndex();
		wpage.clickQuitEditing();
		wpage.assertText("Welcome");
	}
	
	@Test
	public void deleteCurrentWikiTakesUserToWelcomeWiki() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNewPage();
		wpage.setTitle("Bridge Page 1");
		wpage.setHTML("Bridge");
		wpage.submit();
		wpage.deletePage("Bridge Page 1");
		wpage.assertText("Welcome");
	}
	
	@Test
	public void canCreateALinkToAnotherPage() {
		// Create page
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNewPage();
		wpage.setTitle("Bridge Page 2");
		wpage.setHTML("Bridge");
		wpage.submit();
		
		// Link to page.
		wpage.clickPageToEdit("Welcome Page");
		wpage.clickInsertLinkButton();
		
		wpage.clickDialogPagesTab();
		wpage.clickDialogLinkToPage("Bridge Page 2");
		wpage.clickDialogOkButton();
		
		wpage.assertThereIsLinkInEditor();
		
		// Delete page
		wpage.deletePage("Bridge Page 2");
		wpage.assertText("Welcome");
	}
	
	public void canUploadAndInsertAnImage() {
		// This runs up against the inability of webdriver to 
		// script the file upload process.
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNewPage();
		wpage.setTitle("Bridge Page 3");
		
		wpage.clickInsertImageButton();
		/*
		wpage.clickUploadTab();
		wpage.clickUploadField(); // how do we select a file...
		wpage.clickSendInToTheServerButton();
		*/
	}
	
	@Test
	public void userNavigationHighlightsWhenLoaded() throws MalformedURLException {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNewPage();
		wpage.setTitle("Bridge Page 4");
		wpage.setHTML("Bridge");
		wpage.submit();
		
		wpage.clickNavigationTab();
		wpage.clickEditIndex();
		String link = wpage.getLinkForPage("Bridge Page 4");
		wpage.setHTML("<ul><li><a href='"+link+"'>Bridge Page 4</a></li>");
		wpage.submit();
		
		wpage.clickQuitEditing();
		
		CommunityPage page = driver.getCommunityPage();
		page.clickPage("Bridge Page 4");
		page.assertNavigationElementForPageHighlighted("Bridge Page 4");
		
		// Delete Page
		wpage = driver.getCommunityWikiPage();
		wpage.deletePage("Bridge Page 4");
		
		// Fix navigation
		wpage.clickNavigationTab();
		wpage.clickEditIndex();
		wpage.setHTML("Index");
		wpage.submit();
	}
	
	@Test
	public void cannotSaveDangerousHTML() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNavigationTab();
		wpage.clickEditIndex();
		
		wpage.setHTML("<p><script>alert('foo')</script>This should not be saved.</p>");
		wpage.submit();
		wpage.assertThereIsNoScriptInEditor();
	}
	
	@Test
	public void canSaveIndexWikiWithoutContent() {
		CommunityWikiPage wpage = driver.getCommunityWikiPage();
		wpage.getEmbeddedSignIn().signInAsTimPowers();
		wpage.clickNavigationTab();
		wpage.clickEditIndex();
		
		wpage.setHTML("");
		wpage.submit();
		driver.assertNotice("Page saved");
		wpage.clickQuitEditing();
		wpage.assertNavEntryExists("Forums");
	}
}
