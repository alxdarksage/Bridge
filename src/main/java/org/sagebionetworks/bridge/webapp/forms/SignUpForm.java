package org.sagebionetworks.bridge.webapp.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.sagebionetworks.repo.model.auth.NewUser;

public class SignUpForm {

    @Email
    @NotEmpty
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public NewUser getNewUser() {
        NewUser user = new NewUser();
        user.setEmail(this.email);
        // You have to set all this shit to register?
        user.setFirstName("Alx");
        user.setLastName("Dark");
        user.setPassword("password");
        user.setDisplayName("displayname");
        user.setAcceptsTermsOfUse(true);
        return user;
    }
    
}
