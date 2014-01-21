package org.sagebionetworks.bridge.webapp.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = SynapseUserNameValidator.class)
@Documented
public @interface SynapseUserName {

	// User names
	// public static final String VALID_USERNAME_REGEX = "^[A-Za-z0-9._-]{3,}"
    String message() default "org.sagebionetworks.bridge.webapp.validators.SynapseUserName.message";
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};    
	
}
