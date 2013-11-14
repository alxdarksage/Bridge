package org.sagebionetworks.bridge.webapp.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.sagebionetworks.repo.model.auth.NewUser;

public class SignUpForm {

	@NotEmpty
	private String displayName;

	@Email
	@NotEmpty
	private String email;

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

	public NewUser getNewUser() {
		NewUser user = new NewUser();
		user.setEmail(this.email);
		user.setDisplayName(this.displayName);
		return user;
	}
}
