package org.sagebionetworks.bridge.webapp.forms;

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.sagebionetworks.repo.model.auth.NewUser;

public class SignUpForm {

	@NotEmpty
	private String displayName;

	@Email
	@NotEmpty
	private String email;

	@AssertTrue
	private boolean acceptTermsOfUse;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean getAcceptTermsOfUse() {
		return acceptTermsOfUse;
	}

	public void setAcceptTermsOfUse(boolean acceptTermsOfUse) {
		this.acceptTermsOfUse = acceptTermsOfUse;
	}

	public NewUser getNewUser() {
		NewUser user = new NewUser();
		user.setEmail(this.email);
		user.setDisplayName(this.displayName);
		user.setAcceptsTermsOfUse(false);
		return user;
	}
}
