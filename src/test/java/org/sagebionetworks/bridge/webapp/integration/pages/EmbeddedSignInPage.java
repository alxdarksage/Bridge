package org.sagebionetworks.bridge.webapp.integration.pages;


abstract public class EmbeddedSignInPage {
	
	protected WebDriverFacade facade;
	
	public EmbeddedSignInPage(WebDriverFacade facade) {
		this.facade = facade;
	}

	public SignInPage getEmbeddedSignIn() {
		return new SignInPage(this.facade);
	}
}
