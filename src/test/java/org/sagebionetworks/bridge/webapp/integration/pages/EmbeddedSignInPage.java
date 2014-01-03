package org.sagebionetworks.bridge.webapp.integration.pages;

abstract public class EmbeddedSignInPage {
	
	protected WebDriverFacade facade;
	
	public EmbeddedSignInPage(WebDriverFacade facade) {
		this.facade = facade;
	}

	public SignInPage getEmbeddedSignIn() {
		facade.waitUntil("#signInForm");
		return new SignInPage(this.facade);
	}
	
	public ProfilePanelPage getProfilePanelPage() {
		facade.waitUntil("#profile-pane");
		return new ProfilePanelPage(this.facade);
	}
}
