package org.sagebionetworks.bridge.webapp.forms;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.sagebionetworks.bridge.webapp.validators.FieldMatch;

@FieldMatch.List({
    @FieldMatch(first = "password", second = "passwordConfirm", message = "PasswordDoesntMatch")
})
public class ResetPasswordForm {

	@NotEmpty
	@Length(min=6, max=30)
	private String password;
	
	@NotEmpty
	private String passwordConfirm;
	
	@NotEmpty
	private String token;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordConfirm() {
		return passwordConfirm;
	}
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
