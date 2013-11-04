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
	private boolean acceptsTermsOfUse;

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

	public boolean isAcceptsTermsOfUse() {
		return acceptsTermsOfUse;
	}

	public void setAcceptsTermsOfUse(boolean acceptsTermsOfUse) {
		this.acceptsTermsOfUse = acceptsTermsOfUse;
	}

	public NewUser getNewUser() {
		NewUser user = new NewUser();
		user.setEmail(this.email);
		user.setDisplayName(this.displayName);
		user.setAcceptsTermsOfUse(true);
		return user;
	}
}
