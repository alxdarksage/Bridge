package org.sagebionetworks.bridge.webapp.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SynapseUserNameValidator implements ConstraintValidator<SynapseUserName, String> {

	@Override
	public void initialize(SynapseUserName constraintAnnotation) {
	}

	@Override
	public boolean isValid(String name, ConstraintValidatorContext context) {
		return (name != null && name.matches("^[A-Za-z0-9._-]{3,}"));
	}

}
