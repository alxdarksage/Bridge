package org.sagebionetworks.bridge.webapp.integration.pages;


public class TermsOfUsePage {
	
	public static final String TITLE = "Terms of Use";
	
	private WebDriverFacade facade;
	
	public TermsOfUsePage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void checkTermsOfUseAgreement() {
		facade.click("#acceptTermsOfUse");
	}

	public void assertAgreementError() {
		facade.waitUntil("#acceptTermsOfUse_errors");
		facade.assertErrorMessage("#acceptTermsOfUse_errors", "You must accept the terms of use before signing in to Bridge.");
	}
	
	public void cancel() {
		facade.click("#cancelAct");
	}
	
	public void submit() {
		facade.submit("#termsOfUseForm");
	}
	
}
