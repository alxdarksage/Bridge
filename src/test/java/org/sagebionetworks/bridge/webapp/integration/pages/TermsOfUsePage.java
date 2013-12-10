package org.sagebionetworks.bridge.webapp.integration.pages;


public class TermsOfUsePage {
	
	public static final String TITLE = "Terms of Use";
	public static final String URL = "/termsOfUse.html";
	
	private WebDriverFacade facade;
	
	public TermsOfUsePage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	public void checkTermsOfUseAgreement() {
		facade.click("#acceptTermsOfUse");
	}
	
	public void clickCancel() {
		facade.click("#cancelAct");
	}
	
	public void assertAgreementError() {
		facade.assertErrorMessage("#acceptTermsOfUse_errors", "You must accept the terms of use before signing in to Bridge.");
	}
	
	public void submit() {
		facade.submit("#termsOfUseForm");
	}
	
}
