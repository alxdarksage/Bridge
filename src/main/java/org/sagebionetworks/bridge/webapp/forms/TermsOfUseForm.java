package org.sagebionetworks.bridge.webapp.forms;

import javax.validation.constraints.AssertTrue;

public class TermsOfUseForm {

	private String termsOfUse;

	@AssertTrue
	private boolean acceptTermsOfUse;
	
	private boolean oauthRedirect;

	public String getTermsOfUse() {
		return termsOfUse;
	}
	public void setTermsOfUse(String termsOfUse) {
		this.termsOfUse = termsOfUse;
	}
	public boolean getAcceptTermsOfUse() {
		return acceptTermsOfUse;
	}
	public void setAcceptTermsOfUse(boolean acceptTermsOfUse) {
		this.acceptTermsOfUse = acceptTermsOfUse;
	}
	public boolean isOauthRedirect() {
		return oauthRedirect;
	}
	public void setOauthRedirect(boolean oauthRedirect) {
		this.oauthRedirect = oauthRedirect;
	}
}
